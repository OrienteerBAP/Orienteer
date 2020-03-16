package org.orienteer.notifications.resource;

import org.apache.wicket.request.resource.AbstractResource;
import org.orienteer.core.MountPath;

/**
 * Resource for handle SMS notification status
 * TODO: implement this resource
 */
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
