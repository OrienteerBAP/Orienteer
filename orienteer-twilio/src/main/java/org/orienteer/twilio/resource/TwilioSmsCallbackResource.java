package org.orienteer.twilio.resource;

import org.apache.http.HttpStatus;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.orienteer.core.MountPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource for handle information about sms
 * TODO: implement this resource
 */
@MountPath("/api/sms/callback")
public class TwilioSmsCallbackResource extends AbstractResource {

  private static final Logger LOG = LoggerFactory.getLogger(TwilioSmsCallbackResource.class);

  @Override
  protected ResourceResponse newResourceResponse(Attributes attributes) {
    ResourceResponse response = new ResourceResponse();
    if (response.dataNeedsToBeWritten(attributes)) {
      LOG.info("SMS callback");

      PageParameters parameters = attributes.getParameters();

      parameters.getNamedKeys().forEach(key -> LOG.info("{} -> {}", key, parameters.get(key).toOptionalString()));

      response.setStatusCode(HttpStatus.SC_OK);
    }
    return response;
  }
}
