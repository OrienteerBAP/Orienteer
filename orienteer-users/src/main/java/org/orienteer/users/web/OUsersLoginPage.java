package org.orienteer.users.web;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.MountPath;
import org.orienteer.core.web.LoginPage;
import org.orienteer.users.component.OUsersLoginPanel;
import org.orienteer.users.repository.OAuth2Repository;
import org.orienteer.users.service.IOAuth2Service;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

@MountPath("/login")
public class OUsersLoginPage extends LoginPage {

    @Inject
    private IOAuth2Service authService;

    public OUsersLoginPage() {
        super();
    }

    public OUsersLoginPage(PageParameters params) {
        this();
        String code = params.get("code").toOptionalString();
        String state = params.get("state").toOptionalString();

        if (!Strings.isNullOrEmpty(code) && !Strings.isNullOrEmpty(state)) {
            OAuth2Repository.getServiceContextByState(state)
                    .ifPresent(ctx -> {
                        boolean authorized = authService.authorize(ctx.getService(), code);
                        ctx.setUsed(true);
                        DBClosure.sudoConsumer(db -> ctx.save());
                        if (authorized) {
                            continueToOriginalDestination();
                            setResponsePage(getApplication().getHomePage());
                        }
                    });
        }
    }

    @Override
    protected WebMarkupContainer createLoginPanel(String id) {
        return new OUsersLoginPanel(id);
    }
}
