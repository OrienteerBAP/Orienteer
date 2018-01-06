package org.orienteer.core.resource;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.time.Time;
import org.orienteer.core.OrienteerWebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Share dynamic resources such as image, video and other.
 */
public class OContentShareResource extends AbstractResource {
    private static final Logger LOG = LoggerFactory.getLogger(OContentShareResource.class);

    public static final String MOUNT_PATH = "/content/${rid}/${file}";
    public static final String RES_KEY    = OContentShareResource.class.getSimpleName();

    public static URL urlFor(String field, ODocument document) {
        try {
            Map<String, String> map = new HashMap<>(2);
            map.put("file", field);
            map.put("rid", document.getIdentity().toString().substring(1));
            HttpServletRequest request = (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();
            String path = new StrSubstitutor(map).replace(MOUNT_PATH);
            return new URL(request.getScheme() + "://" + request.getHeader("host") + path);
        } catch (MalformedURLException e) {
            LOG.error("Can't create url for attachment with field = {}, document = {}", field, document, e);
        }
        return null;
    }

    @Override
    protected ResourceResponse newResourceResponse(IResource.Attributes attributes) {
        final ResourceResponse response = new ResourceResponse();
        response.setLastModified(Time.now());
        if (response.dataNeedsToBeWritten(attributes)) {
            PageParameters params = attributes.getParameters();
            ODocument document = getDocumentByRid(params.get("rid").toOptionalString());
            if (document != null) {
                final byte [] data = document.field(params.get("file").toOptionalString());
                if (data.length > 0) {
                    response.setWriteCallback(new WriteCallback() {
                        @Override
                        public void writeData(IResource.Attributes attributes) throws IOException {
                            attributes.getResponse().write(data);
                        }
                    });
                }
            } else {
                response.setError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
        return response;
    }

    private ODocument getDocumentByRid(String rid) {
        return !Strings.isNullOrEmpty(rid) ? (ODocument) OrienteerWebApplication.get().getDatabase().getRecord(new ORecordId(rid)) : null;
    }
}
