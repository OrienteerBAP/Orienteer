package org.orienteer.notifications.model;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.notifications.service.IONotificationTransportFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ONotification extends ODocumentWrapper {

  public static final String CLASS_NAME = "ONotification";

  public static final String PROP_ID               = "id";
  public static final String PROP_STATUS_HISTORIES = "statusHistories";
  public static final String PROP_STATUS           = "status";
  public static final String PROP_TRANSPORT        = "transport";

  protected ONotification(String iClassName) {
    super(iClassName);
  }

  protected ONotification(ODocument iDocument) {
    super(iDocument);
  }

  public String getId() {
    return document.field(PROP_ID);
  }

  public ONotification setId(String id) {
    document.field(PROP_ID);
    return this;
  }

  public ONotificationStatus getStatus() {
    ODocument status = getStatusAsDocument();
    return status != null ? new ONotificationStatus(status) : null;
  }

  public ODocument getStatusAsDocument() {
    OIdentifiable status = document.field(PROP_STATUS);
    return status != null ? status.getRecord() : null;
  }

  public ONotification setStatus(ONotificationStatus status) {
    return setStatusAsDocument(status != null ? status.getDocument() : null);
  }

  public ONotification setStatusAsDocument(ODocument status) {
    document.field(PROP_STATUS, status);
    return this;
  }

  public ONotificationTransport getTransport() {
    ODocument transport = getTransportAsDocument();
    if (transport != null) {
      return OrienteerWebApplication.lookupApplication()
              .getServiceInstance(IONotificationTransportFactory.class)
              .create(transport);
    }
    return null;
  }

  public ODocument getTransportAsDocument() {
    OIdentifiable transport = document.field(PROP_TRANSPORT);
    return transport != null ? transport.getRecord() : null;
  }

  public ONotification setTransport(ONotificationTransport transport) {
    return setTransportAsDocument(transport != null ? transport.getDocument() : null);
  }

  public ONotification setTransportAsDocument(ODocument transport) {
    document.field(PROP_TRANSPORT, transport);
    return this;
  }

  public List<ONotificationStatusHistory> getStatusHistories() {
    return getStatusHistoriesAsDocuments().stream()
            .map(ONotificationStatusHistory::new)
            .collect(Collectors.toCollection(LinkedList::new));
  }

  public List<ODocument> getStatusHistoriesAsDocuments() {
    return CommonUtils.getDocuments(document.field(PROP_STATUS_HISTORIES));
  }

  public ONotification setStatusHistories(List<ONotificationStatusHistory> statusHistories) {
    List<ODocument> docs = statusHistories.stream()
            .map(ONotificationStatusHistory::getDocument)
            .collect(Collectors.toCollection(LinkedList::new));
    return setStatusHistoriesAsDocuments(docs);
  }

  public ONotification setStatusHistoriesAsDocuments(List<ODocument> statusHistories) {
    document.field(PROP_STATUS_HISTORIES, statusHistories);
    return this;
  }

  public ONotification addStatusHistory(ONotificationStatusHistory history) {
    return history != null ? addStatusHistory(history.getDocument()) : this;
  }

  public ONotification addStatusHistory(ODocument history) {
    List<ODocument> histories = new LinkedList<>(getStatusHistoriesAsDocuments());
    histories.add(history);
    return setStatusHistoriesAsDocuments(histories);
  }
}
