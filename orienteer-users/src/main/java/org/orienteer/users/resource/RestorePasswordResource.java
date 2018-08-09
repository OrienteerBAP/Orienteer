package org.orienteer.users.resource;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.util.time.Time;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.service.IOrienteerUsersService;
import org.orienteer.users.util.OUsersDbUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Restore resource which handles users which opens this resource.
 * Redirect user to {@link IOrienteerUsersService#getRestorePasswordPage()} if user with given restore id exists in system.
 * Otherwise redirect user to {@link OrienteerWebApplication#getHomePage()}
 */
public class RestorePasswordResource extends AbstractResource {

    public static final String MOUNT_PATH = "/restore/${id}/";
    public static final String RES_KEY    = RestorePasswordResource.class.getName();

    public static final String PARAMETER_ID = "id";

    @Inject
    private IOrienteerUsersService service;

    public static String getLinkForUser(OSecurityUser user) {
        return getLinkForUser(user.getDocument());
    }

    public static String getLinkForUser(ODocument doc) {
        String id = doc.field(OrienteerUser.PROP_RESTORE_ID);
        PageParameters params = new PageParameters();
        params.add(PARAMETER_ID, id);
        CharSequence url = RequestCycle.get().urlFor(new SharedResourceReference(RES_KEY), params);
        return RequestCycle.get().getUrlRenderer().renderFullUrl(Url.parse(url));
    }

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        ResourceResponse response = new ResourceResponse();
        response.setLastModified(Time.now());
        if (response.dataNeedsToBeWritten(attributes)) {
            response.setWriteCallback(createWriteCallback());
            response.setStatusCode(HttpServletResponse.SC_OK);
        }
        return response;
    }

    private WriteCallback createWriteCallback() {
        return new WriteCallback() {
            @Override
            public void writeData(Attributes attributes) throws IOException {
                String id = attributes.getParameters().get("id").toString();
                PageParameters params = new PageParameters();
                if (!Strings.isNullOrEmpty(id) && OUsersDbUtils.isUserExistsWithRestoreId(id)) {
                    params.add(RES_KEY, id);
                    RequestCycle.get().setResponsePage(service.getRestorePasswordPage(), params);
                } else RequestCycle.get().setResponsePage(OrienteerWebApplication.lookupApplication().getHomePage());
            }
        };
    }

    public static void mount(OrienteerWebApplication app) {
        app.getSharedResources().add(RES_KEY, app.getServiceInstance(RestorePasswordResource.class));
        app.mountResource(MOUNT_PATH, new SharedResourceReference(RES_KEY));
    }

    public static void unmount(OrienteerWebApplication app) {
        app.getSharedResources().remove(app.getSharedResources().get(RES_KEY).getKey());
        app.unmount(MOUNT_PATH);
    }
}
