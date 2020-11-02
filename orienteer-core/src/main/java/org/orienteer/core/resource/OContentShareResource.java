package org.orienteer.core.resource;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tika.Tika;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.Url.StringMode;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Time;
import org.orienteer.core.MountPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Share dynamic resources such as image, video and other. Params
 * type (optional) - content type of the content
 * s (optional) - size of image to resize to
 * q (optional) - quality of output image after resizing
 */
@MountPath("/content/${rid}/${field}")
public class OContentShareResource extends AbstractResource {

  private static final Logger LOG = LoggerFactory.getLogger(OContentShareResource.class);

  public static SharedResourceReference getSharedResourceReference() {
    return new SharedResourceReference(OContentShareResource.class.getName());
  }

  public static CharSequence urlFor(ODocument document, String field, String contentType, boolean fullUrl) {
    return urlFor(getSharedResourceReference(), document, field, contentType, null, null, fullUrl);
  }

  public static CharSequence urlFor(ODocument document, String field, String contentType, Integer imageSize, boolean fullUrl) {
    return urlFor(getSharedResourceReference(), document, field, contentType, imageSize, null, fullUrl);
  }

  public static CharSequence urlFor(ODocument document, String field, String contentType, Integer imageSize, Double imageQuality, boolean fullUrl) {
    return urlFor(getSharedResourceReference(), document, field, contentType, imageSize, imageQuality, fullUrl);
  }

  protected static CharSequence urlFor(ResourceReference ref, ODocument document, String field, String contentType, Integer imageSize, Double imageQuality, boolean fullUrl) {
    PageParameters params = new PageParameters();
    params.add("rid", document.getIdentity().toString().substring(1));
    params.add("field", field);
    params.add("v", document.getVersion());
    if (!Strings.isEmpty(contentType)) {
      params.add("type", contentType);
    }
    if (imageSize != null && imageSize > 0) {
      params.add("s", imageSize);
    }
    if (imageQuality != null && imageQuality > 0 && imageQuality < 1.0) {
      params.add("q", imageQuality);
    }
    if (fullUrl) {
      return RequestCycle.get().getUrlRenderer()
              .renderFullUrl(Url.parse(RequestCycle.get().urlFor(ref, params)));
    } else {
      return RequestCycle.get().mapUrlFor(ref, params).toString(StringMode.LOCAL);
    }
  }

  @Override
  protected ResourceResponse newResourceResponse(IResource.Attributes attributes) {
    final ResourceResponse response = new ResourceResponse();
    response.setLastModified(Time.now());

    if (response.dataNeedsToBeWritten(attributes)) {
      PageParameters params = attributes.getParameters();
      String ridStr = "#" + params.get("rid").toOptionalString();
      ORID orid = ORecordId.isA(ridStr) ? new ORecordId(ridStr) : null;
      ResponseData data = getData(orid, params);

      if (data != null) {
        if (isCacheAllowed()) {
          if (params.get("v").isEmpty()) {
            response.disableCaching();
          } else {
            response.setCacheDurationToMaximum();
          }
        }
        response.setContentType(data.contentType);
        response.setWriteCallback(createWriteCallback(data.data));
      }

      if (response.getWriteCallback() == null) {
        response.setError(HttpServletResponse.SC_NOT_FOUND);
      }
    }
    return response;
  }

  protected boolean isCacheAllowed() {
    return false;
  }

  protected byte[] getContent(OIdentifiable rid, String field) {
    ODocument doc = rid.getRecord();
    if (doc == null) return null;
    return doc.field(field, byte[].class);
  }

  private WriteCallback createWriteCallback(byte[] data) {
    return new WriteCallback() {
      @Override
      public void writeData(IResource.Attributes attributes) throws IOException {
        attributes.getResponse().write(data);
      }
    };
  }

  private ResponseData getData(ORID rid, PageParameters params) {
    if (rid == null) {
      return null;
    }

    String field = params.get("field").toString();
    ResponseData data = ResponseData.of(getContent(rid, field));
    if (data != null && data.contentType.startsWith("image/")) {
      Integer maxSize = params.get("s").toOptionalInteger();
      if (maxSize != null && maxSize > 0) {
        try {
          data.data = resizeImage(data.data, maxSize);
        } catch (IOException e) {
          LOG.error("Can't create thumbnail. Using original image. rid = {}, field = {}, contentType = {}, size = {} b, maxSize = {} b",
                  rid, field, data.contentType, data.data.length, maxSize, e);
        }
      }
    }
    return data;
  }

  private byte[] resizeImage(byte[] image, int maxSize) throws IOException {
    byte[] data = image;

    while (data.length > maxSize) {
      ByteArrayOutputStream thumbnailOS = new ByteArrayOutputStream();
      Thumbnails.of(new ByteArrayInputStream(data))
              .scale(0.8)
              .toOutputStream(thumbnailOS);
      data = thumbnailOS.toByteArray();
    }

    return data;
  }

  private static class ResponseData {
    private byte[] data;
    private String contentType;

    public static ResponseData of(byte[] data) {
      if (data == null || data.length == 0) {
        return null;
      }
      return new ResponseData(data);
    }

    public ResponseData(byte[] data) {
      this(data, new Tika().detect(data));
    }

    public ResponseData(byte[] data, String contentType) {
      this.data = data;
      this.contentType = contentType;
    }
  }
}
