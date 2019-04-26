package org.orienteer.users.resource;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.web.LoginPage;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.repository.OrienteerUserModuleRepository;
import org.orienteer.users.repository.OrienteerUserRepository;
import org.orienteer.users.service.IOrienteerUsersService;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.io.IOException;

/**
 * Registration resource which handles users which opens this resource.
 * Redirect user to {@link IOrienteerUsersService#getRegistrationPage()} with parameters which contains user id
 * if user exists in system but account status is {@link com.orientechnologies.orient.core.metadata.security.OUser.STATUSES#SUSPENDED}.
 * Otherwise redirect user to {@link IOrienteerUsersService#getRegistrationPage()}
 */
public class RegistrationResource extends AbstractResource {

    public static final String MOUNT_PATH = "/r/registration";
    public static final String RES_KEY    = RegistrationResource.class.getName();

    public static final String PARAMETER_ID = "id";

    public static String createRegistrationLink(OrienteerUser user) {
        PageParameters params = new PageParameters();
        params.add(PARAMETER_ID, user.getId());
        CharSequence url = RequestCycle.get().urlFor(new SharedResourceReference(RES_KEY), params);
        return RequestCycle.get().getUrlRenderer().renderFullUrl(Url.parse(url));
    }

    @Override
    protected ResourceResponse newResourceResponse(Attributes attrs) {
        ResourceResponse response = new ResourceResponse();
        if (response.dataNeedsToBeWritten(attrs)) {
            if (OrienteerUserModuleRepository.isRegistrationActive()) {
                PageParameters params = attrs.getParameters();
                String id = params.get(PARAMETER_ID).toOptionalString();

                if (!Strings.isNullOrEmpty(id)) {
                    OrienteerUserRepository.getUserById(id)
                            .filter(user -> user.getAccountStatus() != OSecurityUser.STATUSES.ACTIVE)
                            .ifPresent(user -> {
                                user.setAccountStatus(OSecurityUser.STATUSES.ACTIVE);
                                DBClosure.sudoSave(user);
                                response.setWriteCallback(createCallback(true));
                            });
                }
            }

            if (response.getWriteCallback() == null) {
                response.setWriteCallback(createCallback(false));
            }
        }

        return response;
    }

    private WriteCallback createCallback(boolean success) {
        return new WriteCallback() {
            @Override
            public void writeData(Attributes attributes) throws IOException {
                PageParameters params = new PageParameters();
                if (success) {
                    params.set(PARAMETER_ID, attributes.getParameters().get(PARAMETER_ID).toOptionalString());
                }
                RequestCycle.get().setResponsePage(LoginPage.class, params);
            }
        };
    }

    public static void mount(OrienteerWebApplication app) {
        RegistrationResource resource = app.getServiceInstance(RegistrationResource.class);
        app.getSharedResources().add(RES_KEY, resource);
        app.mountResource(MOUNT_PATH, new SharedResourceReference(RES_KEY));
    }

    public static void unmount(OrienteerWebApplication app) {
        app.getSharedResources().remove(app.getSharedResources().get(RES_KEY).getKey());
        app.unmount(MOUNT_PATH);
    }
}
