package org.orienteer.notifications.scheduler;

import com.orientechnologies.orient.core.Orient;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Notification scheduler. Temporary fix. Scheduler in OrientDB 2 has connection leak.
 * Need upgrade to OrientDB 3
 */
public final class ONotificationScheduler {

  private static final ConcurrentHashMap<String, ONotificationTask> TASKS = new ConcurrentHashMap<>();

  private ONotificationScheduler() {}

  public static void scheduleTask(ONotificationTask task, Date firstTime) {
    scheduleTask(task, firstTime, 0);
  }

  public static void scheduleTask(ONotificationTask task, long delay) {
    scheduleTask(task, delay, 0);
  }

  public static void scheduleTask(ONotificationTask task, Date firstTime, long period) {
    stopTask(task.getName());
    TASKS.put(task.getName(), task);
    Orient.instance().scheduleTask(task, firstTime, period);
  }

  public static void scheduleTask(ONotificationTask task, long delay, long period) {
    stopTask(task.getName());
    TASKS.put(task.getName(), task);
    Orient.instance().scheduleTask(task, delay, period);
  }

  public static synchronized void stopTask(String name) {
    ONotificationTask task = TASKS.remove(name);

    if (task != null) {
      task.cancel();
    }
  }

  public static void stopAll() {
    TASKS.keySet().forEach(ONotificationScheduler::stopTask);
  }

}
