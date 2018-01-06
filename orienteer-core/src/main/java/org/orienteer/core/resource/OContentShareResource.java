package org.orienteer.core.resource;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletResponse;

import org.apache.tika.Tika;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Time;
import org.orienteer.core.OrienteerWebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Share dynamic resources such as image, video and other.
 */
public class OContentShareResource extends AbstractResource {
    private static final Logger LOG = LoggerFactory.getLogger(OContentShareResource.class);

    public static final String MOUNT_PATH = "/content/${rid}/${field}";
    public static final String RES_KEY    = OContentShareResource.class.getSimpleName();
    
    @Inject
    private Provider<ODatabaseDocument> dbProvider;

    public static CharSequence urlFor(ODocument document, String field, String contentType, boolean fullUrl) {
    	
    	PageParameters params = new PageParameters();
    	params.add("rid", document.getIdentity().toString().substring(1));
    	params.add("field", field);
    	if(!Strings.isEmpty(contentType)) params.add("type", contentType);
    	CharSequence url = RequestCycle.get().urlFor(new SharedResourceReference(RES_KEY), params);
    	if(fullUrl) {
    		url = RequestCycle.get().getUrlRenderer().renderFullUrl(Url.parse(url));
    	}
    	return url;
    }

    @Override
    protected ResourceResponse newResourceResponse(IResource.Attributes attributes) {
        final ResourceResponse response = new ResourceResponse();
        response.setLastModified(Time.now());
        if (response.dataNeedsToBeWritten(attributes)) {
            PageParameters params = attributes.getParameters();
            String rid = "#"+params.get("rid").toOptionalString();
            ODocument document = ORecordId.isA(rid) ? (ODocument)dbProvider.get().load(new ORecordId(rid)) : null;
            if (document != null) {
                final byte [] data = document.field(params.get("field").toString(), byte[].class);
                if (data != null && data.length > 0) {
                	String contentType = params.get("type").toOptionalString();
                	if(Strings.isEmpty(contentType)) {
                		contentType = new Tika().detect(data);
                	}
                    response.setContentType(contentType);
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
    
    public static void mount(OrienteerWebApplication app) {
    	app.getSharedResources().add(OContentShareResource.RES_KEY, app.getServiceInstance(OContentShareResource.class));
		app.mountResource(OContentShareResource.MOUNT_PATH, new SharedResourceReference(OContentShareResource.RES_KEY));
    }

}
