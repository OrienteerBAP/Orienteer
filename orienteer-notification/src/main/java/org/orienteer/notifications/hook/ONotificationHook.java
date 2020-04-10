package org.orienteer.notifications.hook;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.OValidationException;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.notifications.model.IONotification;
import org.orienteer.notifications.model.IONotificationDAO;
import org.orienteer.notifications.model.IONotificationStatusHistory;
import org.orienteer.notifications.service.IONotificationFactory;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Hook for {@link IONotification}
 */
public class ONotificationHook extends ODocumentHookAbstract {

  public ONotificationHook(ODatabaseDocument database) {
    super(database);
    setIncludeClasses(IONotification.CLASS_NAME);
  }

  @Override
  public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
    return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
  }

  @Override
  public RESULT onRecordBeforeCreate(ODocument doc) {
    boolean changed = false;

    IONotification notification = OrienteerWebApplication.lookupApplication().getServiceInstance(IONotificationFactory.class)
            .create(doc);

    if (notification == null) {
      throw new OValidationException("Not supported '" + IONotification.CLASS_NAME + "' subclass!");
    }

    if (notification.getId() == null) {
      notification.setId(UUID.randomUUID().toString());
      changed = true;
    }

    if (notification.getCreated() == null) {
      notification.setCreated(new Date());
      changed = true;
    }

    if (notification.getStatus() == null) {
      ODocument pendingStatus = IONotificationDAO.get().getPendingStatus();

      notification.setStatus(pendingStatus);

      IONotificationStatusHistory history = IONotificationStatusHistory.create(new Date(), pendingStatus);
      history.save();

      notification.addStatusHistory(history);

      changed = true;
    }


    return changed ? RESULT.RECORD_CHANGED : super.onRecordBeforeCreate(doc);
  }

  @Override
  public RESULT onRecordBeforeDelete(ODocument doc) {
    boolean changed = false;

    IONotification notification = OrienteerWebApplication.lookupApplication().getServiceInstance(IONotificationFactory.class)
            .create(doc);

    if (notification == null) {
      throw new OValidationException("Not supported '" + IONotification.CLASS_NAME + "' subclass!");
    }

    List<ODocument> history = notification.getStatusHistories();
    if (!history.isEmpty()) {
      history.forEach(database::delete);
      notification.setStatusHistories(null);
      changed = true;
    }

    return changed ? RESULT.RECORD_CHANGED : super.onRecordBeforeDelete(doc);
  }
}
