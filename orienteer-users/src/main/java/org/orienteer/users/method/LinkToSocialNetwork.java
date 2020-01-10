package org.orienteer.users.method;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AbstractModalWindowCommand;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.filters.ODocumentFilter;
import org.orienteer.core.method.filters.PlaceFilter;
import org.orienteer.core.method.methods.AbstractModalOMethod;
import org.orienteer.users.component.panel.LinkToSocialNetworkPanel;
import org.orienteer.users.model.OrienteerUser;
import ru.ydn.wicket.wicketorientdb.model.ODocumentWrapperModel;

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
public class LinkToSocialNetwork extends AbstractModalOMethod {
    @Override
    @SuppressWarnings("unchecked")
    public Component getModalContent(String componentId, ModalWindow modal, AbstractModalWindowCommand<?> command) {
        IModel<ODocument> model = (IModel<ODocument>) getContext().getDisplayObjectModel();
        return new LinkToSocialNetworkPanel(componentId, new ODocumentWrapperModel<>(new OrienteerUser(model.getObject())));
    }
}
