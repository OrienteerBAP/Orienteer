package org.orienteer.notifications.task;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.ThreadContext;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.notifications.model.ONotification;
import org.orienteer.notifications.model.ONotificationStatus;
import org.orienteer.notifications.module.ONotificationModule;
import org.orienteer.notifications.repository.ONotificationModuleRepository;
import org.orienteer.notifications.repository.ONotificationRepository;
import org.orienteer.notifications.repository.ONotificationStatusRepository;
import org.orienteer.notifications.scheduler.ONotificationScheduler;
import org.orienteer.notifications.scheduler.ONotificationTask;
import org.orienteer.notifications.service.INotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Task for send notifications
 */
public class ONotificationSendTask extends ONotificationTask {

  private static final Logger LOG = LoggerFactory.getLogger(ONotificationSendTask.class);

  public static final String NAME = "send-notifications";

  public ONotificationSendTask() {
    super(NAME);
  }

  @Override
  public void run() {
    DBClosure.sudoConsumer(db -> {
      try {
        sendNotifications(db);
      } catch (Exception e) {
        LOG.error("Error during {} execution!", getClass().getName(), e);
      } finally {
        rescheduleTask(db);
      }
    });
  }

  private void sendNotifications(ODatabaseDocument db) {
    ONotificationModule.Module module = ONotificationModuleRepository.getModule(db);
    ONotificationStatus pendingStatus = ONotificationStatusRepository.getPendingStatus(db);
    List<ONotification> notifications = ONotificationRepository.getNotificationsByStatus(db, pendingStatus);

    int executorSize = computeExecutorSize(module.getNotificationsPerWorker(), notifications);
    List<List<ONotification>> groupedNotifications = groupNotificationsForWorkers(module.getNotificationsPerWorker(), notifications);
    ExecutorService executorService = Executors.newFixedThreadPool(2);

    List<Future<?>> futures = submitTasks(groupedNotifications, executorService);
    waitForComplete(futures);
  }

  private void waitForComplete(List<Future<?>> futures) {
    futures.forEach(future -> {
      try {
        future.get();
      } catch (Exception e) {
        LOG.error("Error during wait for send notifications!", e);
      }
    });
  }

  private List<Future<?>> submitTasks(List<List<ONotification>> notifications, ExecutorService executorService) {
    return notifications.stream()
            .map(group -> executorService.submit(new SendNotificationsTask(group)))
            .collect(Collectors.toCollection(LinkedList::new));
  }

  private List<List<ONotification>> groupNotificationsForWorkers(int notificationsPerWorker, List<ONotification> notifications) {
    Map<ODocument, LinkedList<List<ONotification>>> groupedMap = new LinkedHashMap<>();

    notifications.stream()
            .collect(Collectors.groupingBy(ONotification::getTransportAsDocument))
            .forEach((transport, notificationsByTransport) -> {
              LinkedList<List<ONotification>> grouped = groupedMap.computeIfAbsent(transport, k -> new LinkedList<>());

              notificationsByTransport.forEach(notification -> {
                List<ONotification> group = grouped.peek();
                if (group == null || group.size() >= notificationsPerWorker) {
                  group = new LinkedList<>();
                  grouped.push(group);
                }
                group.add(notification);
              });
            });

    return groupedMap.values().stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
  }

  private int computeExecutorSize(int notificationsPerWorker, List<ONotification> notifications) {
    if (notifications.size() <= notificationsPerWorker) {
      return 1;
    }
    int maxWorkers = Runtime.getRuntime().availableProcessors() * 4; // send notifications is I/O operation, not CPU at all
    int workers = (int) Math.ceil(notifications.size() / (double) notificationsPerWorker);

    return Math.min(workers, maxWorkers);
  }

  private void rescheduleTask(ODatabaseDocument db) {
    long period = ONotificationModuleRepository.getModule(db).getSendPeriod();
    ONotificationScheduler.scheduleTask(new ONotificationSendTask(), period);
  }

  private static class SendNotificationsTask implements Runnable {

    private final List<ONotification> notifications;

    public SendNotificationsTask(List<ONotification> notifications) {
      this.notifications = notifications;
    }

    @Override
    public void run() {
      OrienteerWebApplication app = OrienteerWebApplication.lookupApplication();
      INotificationService notificationService = app.getServiceInstance(INotificationService.class);
      ThreadContext.setApplication(app);

      DBClosure.sudoConsumer(db -> notificationService.send(notifications));
    }
  }
}
