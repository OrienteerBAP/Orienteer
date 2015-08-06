package org.orienteer.core.component.table;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.vaynberg.wicket.select2.ChoiceProvider;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.Select2Choice;
import com.vaynberg.wicket.select2.TextChoiceProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.orienteer.core.component.meta.ODocumentMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Column for editing {@link com.orientechnologies.orient.core.metadata.schema.OProperty} value of {@link ODocument} via comboBox.
 * @param <T> Type of combobox values.
 */
public class OPropertyValueComboBoxColumn<T> extends AbstractModeMetaColumn<ODocument, DisplayMode, OProperty, String>
{
    private final ChoiceProvider<T> choiceProvider;

    public OPropertyValueComboBoxColumn(OProperty oProperty, ChoiceProvider<T> choiceProvider, IModel<DisplayMode> modeModel) {
        super(new OPropertyModel(oProperty), modeModel);
        this.choiceProvider = choiceProvider;
    }

    @Override
    protected <V> AbstractMetaPanel<ODocument, OProperty, V> newMetaPanel(String componentId, final IModel<OProperty> criteryModel, final IModel<ODocument> rowModel) {
        if (getModeModel().getObject() == DisplayMode.EDIT)
        {
            return new AbstractMetaPanel<ODocument, OProperty, V>(componentId, rowModel, criteryModel) {
                @Override
                protected IModel<String> newLabelModel() {
                    return new OPropertyNamingModel(getCriteryModel());
                }

                @Override
                protected Component resolveComponent(String id, OProperty critery) {
                    return new Select2Choice<T>(id, (IModel<T>)getValueModel(), choiceProvider)
                            .add(new AttributeModifier("style", "width:100%;"));
                }

                @Override
                protected IModel<V> resolveValueModel() {
                    return new DynamicPropertyValueModel<V>(getEntityModel(), getPropertyModel());
                }
            };
        }
        else
        {
            return new ODocumentMetaPanel<V>(componentId, getModeModel(), rowModel, criteryModel);
        }
    }

    @Override
    protected IModel<String> newLabelModel() {
        return new OPropertyNamingModel(getCriteryModel());
    }

    @Override
    public String getCssClass() {
        return "combobox-column";
    }
}
