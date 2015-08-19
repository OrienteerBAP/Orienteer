package org.orienteer.core.component.property;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.vaynberg.wicket.select2.Select2Choice;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.orienteer.core.model.LanguagesChoiceProvider;

/**
 * Panel for editing OLocalization embedded maps.
 * @param <V> the type of collection's objects
 */
public class OLocalizationEditPanel<V> extends EmbeddedMapEditPanel<V> {

    public OLocalizationEditPanel(String id, IModel<ODocument> documentModel, IModel<OProperty> propertyModel) {
        super(id, documentModel, propertyModel);
    }

    @Override
    protected TextField<String> getKeyEditComponent(ListItem<EmbeddedMapEditPanel.Pair<V>> item) {
        Select2Choice<String> select2 = new Select2Choice<String>("key", new PropertyModel<String>(item.getModel(), "key"), LanguagesChoiceProvider.INSTANCE);
        select2.add(new AttributeModifier("style", "width: 100%"));
        return select2;
    }
}

