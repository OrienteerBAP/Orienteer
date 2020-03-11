package org.orienteer.notifications.hook;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.OValidationException;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.notifications.model.ONotification;
import org.orienteer.notifications.model.ONotificationStatusHistory;
import org.orienteer.notifications.repository.ONotificationStatusRepository;
import org.orienteer.notifications.service.IONotificationFactory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ONotificationHook extends ODocumentHookAbstract {

  public ONotificationHook(ODatabaseDocument database) {
    super(database);
    setIncludeClasses(ONotification.CLASS_NAME);
  }

  @Override
  public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
    return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
  }

  @Override
  public RESULT onRecordBeforeCreate(ODocument doc) {
    boolean changed = false;

    ONotification notification = OrienteerWebApplication.lookupApplication().getServiceInstance(IONotificationFactory.class)
            .create(doc);

    if (notification == null) {
      throw new OValidationException("Not supported '" + ONotification.CLASS_NAME + "' subclass!");
    }

    if (notification.getId() == null) {
      notification.setId(UUID.randomUUID().toString());
      changed = true;
    }

    if (notification.getStatusAsDocument() == null) {
      ODocument pendingStatus = ONotificationStatusRepository.getPendingStatus(database).getDocument();

      notification.setStatusAsDocument(pendingStatus);
      notification.addStatusHistory(new ONotificationStatusHistory(Instant.now(), pendingStatus));

      changed = true;
    }


    return changed ? RESULT.RECORD_CHANGED : super.onRecordBeforeCreate(doc);
  }

  @Override
  public RESULT onRecordBeforeDelete(ODocument doc) {
    boolean changed = false;

    ONotification notification = OrienteerWebApplication.lookupApplication().getServiceInstance(IONotificationFactory.class)
            .create(doc);

    if (notification == null) {
      throw new OValidationException("Not supported '" + ONotification.CLASS_NAME + "' subclass!");
    }

    List<ODocument> history = notification.getStatusHistoriesAsDocuments();
    if (!history.isEmpty()) {
      history.forEach(database::delete);
      notification.setStatusHistoriesAsDocuments(null);
      changed = true;
    }

    return changed ? RESULT.RECORD_CHANGED : super.onRecordBeforeDelete(doc);
  }
}
