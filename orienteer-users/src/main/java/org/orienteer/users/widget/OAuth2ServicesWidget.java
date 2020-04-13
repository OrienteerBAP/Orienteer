package org.orienteer.users.widget;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.CreateODocumentCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.widget.AbstractCalculatedDocumentsWidget;
import org.orienteer.core.component.widget.document.CalculatedDocumentsWidget;
import org.orienteer.core.widget.Widget;
import org.orienteer.users.model.IOAuth2Provider;
import org.orienteer.users.model.OAuth2Service;
import org.orienteer.users.module.OrienteerUsersModule;
import org.orienteer.users.util.OUsersCommonUtils;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import java.util.List;

/**
 * Widget which provides possibility for easy configure login throughout social networks
 */
@Widget(
        id = "oauth2-providers",
        selector = OrienteerUsersModule.ModuleModel.CLASS_NAME,
        oClass = AbstractCalculatedDocumentsWidget.WIDGET_OCLASS_NAME,
        tab = "social-networks",
        domain = "document",
        autoEnable = true
)
public class OAuth2ServicesWidget extends CalculatedDocumentsWidget {

    public OAuth2ServicesWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
    }

    @Override
    protected void customizeDataTable(OrienteerDataTable<ODocument, String> table, IModel<DisplayMode> modeModel, IModel<OClass> expectedClass) {
        table.addCommand(newCreateODocumentCommand(table, expectedClass));
        super.customizeDataTable(table, modeModel, expectedClass);
    }

    private CreateODocumentCommand newCreateODocumentCommand(OrienteerDataTable<ODocument, String> table, IModel<OClass> expectedClass) {
        return new CreateODocumentCommand(table, expectedClass) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                List<IOAuth2Provider> providers = OUsersCommonUtils.getOAuth2Providers();
                long size = table.getDataProvider().size();
                setVisible(size < providers.size());
            }
        };
    }

    @Override
    protected String getSql() {
        return String.format("select from %s", OAuth2Service.CLASS_NAME);
    }

    @Override
    protected OClass getExpectedClass(OQueryDataProvider<ODocument> provider) {
        OClass expectedClass = super.getExpectedClass(provider);
        if (expectedClass == null) {
            expectedClass = getDatabase().getMetadata().getSchema().getClass(OAuth2Service.CLASS_NAME);
        }
        return expectedClass;
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.address_book);
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return new ResourceModel("widget.oauth2.services.title");
    }
}
