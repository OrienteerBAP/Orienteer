package org.orienteer.users.widget;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import org.orienteer.users.model.OAuth2Service;

/**
 * Widget which provides possibility for easy configure login throughout social networks
 */
@Widget(
        id = "oauth2-providers",
        oClass = OAuth2Service.CLASS_NAME,
        tab = "providers",
        domain = "document",
        autoEnable = true
)
public class OAuth2ProvidersWidget extends AbstractWidget<ODocument> {

    public OAuth2ProvidersWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.list);
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return Model.of("Providers");
    }
}
