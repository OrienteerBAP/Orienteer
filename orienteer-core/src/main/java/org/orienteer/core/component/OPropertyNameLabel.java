package org.orienteer.core.component;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.CustomAttribute;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;

/**
 * Label for property name
 */
public class OPropertyNameLabel extends GenericPanel<OProperty> {

    public OPropertyNameLabel(String id, IModel<OProperty> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("name", new OPropertyNamingModel(getModel())));
        add(createDescriptionContainer("description"));
    }

    private WebMarkupContainer createDescriptionContainer(String id) {
        return new WebMarkupContainer(id) {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                String description = CustomAttribute.DESCRIPTION.getValue(getModelObject());
                if (!Strings.isNullOrEmpty(description)) {
                    add(AttributeModifier.append("title", description));
                } else {
                    setVisible(false);
                }
            }
        };
    }
}
