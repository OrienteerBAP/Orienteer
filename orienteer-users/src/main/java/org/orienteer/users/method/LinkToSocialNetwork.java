package org.orienteer.users.method;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AbstractModalWindowCommand;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.filters.ODocumentFilter;
import org.orienteer.core.method.filters.PlaceFilter;
import org.orienteer.core.method.methods.AbstractOMethod;
import org.orienteer.users.component.panel.LinkToSocialNetworkPanel;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.repository.OAuth2Repository;
import ru.ydn.wicket.wicketorientdb.model.ODocumentWrapperModel;

/**
 * Method for link user account to user social networks accounts
 */
@OMethod(
        titleKey = "method.user.social.networks.link",
        icon = FAIconType.chain,
        bootstrap = BootstrapType.PRIMARY,
        order = 0,
        filters = {
                @OFilter(fClass = ODocumentFilter.class, fData = OrienteerUser.CLASS_NAME),
                @OFilter(fClass = PlaceFilter.class, fData = "STRUCTURE_TABLE")
        }
)
public class LinkToSocialNetwork extends AbstractOMethod {

    @Override
    @SuppressWarnings("unchecked")
    public Command<?> createCommand(String id) {
        IModel<ODocument> model = (IModel<ODocument>) getContext().getDisplayObjectModel();
        IModel<OrienteerUser> userModel = new ODocumentWrapperModel<>(new OrienteerUser(model.getObject()));

        return new AbstractModalWindowCommand<OrienteerUser>(id, getTitleModel(), userModel) {

            @Override
            protected void onInitialize() {
                super.onInitialize();
                applyVisualSettings(this);
                applyBehaviors(this);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                OrienteerUser user = getModelObject();

                setVisible(user.getSocialNetworks().size() < OAuth2Repository.getOAuth2Services().size());
            }

            @Override
            protected void initializeContent(ModalWindow modal) {
                modal.setTitle(getTitleModel());
                modal.setMinimalWidth(520);
                modal.setMinimalHeight(250);
                modal.setContent(new LinkToSocialNetworkPanel(modal.getContentId(), getModel()));
            }

            @Override
            public void onAfterModalSubmit() {
                sendActionPerformed();
            }

        };
    }
}
