package org.orienteer.users.component;

import com.google.inject.Inject;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.orienteer.users.service.IOrienteerUsersService;

/**
 * Login footer panel.
 * Contains registration link
 */
public class OUsersLoginFooterPanel extends Panel {

    @Inject
    private IOrienteerUsersService usersService;

    public OUsersLoginFooterPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new BookmarkablePageLink<>("registerLink", usersService.getRegistrationPage()));
    }
}
