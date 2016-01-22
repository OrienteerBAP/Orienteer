package org.orienteer.core.component.widget.oclass;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.widget.AbstractSchemaLocalizationWidget;
import org.orienteer.core.widget.Widget;

/**
 * Widget to show and modify {@link com.orientechnologies.orient.core.metadata.schema.OClass} localization.
 */
@Widget(id="class-localization", domain="class", tab="localization", order=20, autoEnable=true)
public class OClassLocalizationWidget extends AbstractSchemaLocalizationWidget<OClass> {

    public OClassLocalizationWidget(String id, IModel<OClass> model, final IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);

    }

    @Override
    protected String getLocalizationKey(OClass oProperty) {
        return getModelObject().getName();
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return new ResourceModel("class.localization");
    }
}
