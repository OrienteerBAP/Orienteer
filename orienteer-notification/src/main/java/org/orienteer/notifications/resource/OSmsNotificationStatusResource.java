package org.orienteer.notifications.resource;

import org.apache.wicket.request.resource.AbstractResource;
import org.orienteer.core.MountPath;

@MountPath("/api/notification/sms")
public class OSmsNotificationStatusResource extends AbstractResource {
  @Override
  protected ResourceResponse newResourceResponse(Attributes attributes) {
    ResourceResponse response = new ResourceResponse();
    if (response.dataNeedsToBeWritten(attributes)) {

    }
    return response;
  }
}
