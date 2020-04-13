package org.orienteer.notifications.scheduler;

import java.util.TimerTask;

/**
 * Notification task
 */
public abstract class ONotificationTask extends TimerTask {

  private final String name;

  public ONotificationTask(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
