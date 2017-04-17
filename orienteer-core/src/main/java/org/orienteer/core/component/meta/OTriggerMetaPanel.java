package org.orienteer.core.component.meta;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.model.OFunctionTextChoiceProvider;
import org.orienteer.core.model.OTriggerModel;
import org.wicketstuff.select2.Select2Choice;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

import java.util.Arrays;
import java.util.List;

import static com.orientechnologies.orient.core.db.record.OClassTrigger.*;

/**
 * Meta panel for {@link OTriggerModel}
 *
 * @param <V> type of a value
 */
public class OTriggerMetaPanel<V> extends AbstractComplexModeMetaPanel<OTriggerModel, DisplayMode, String, V> implements IDisplayModeAware
{

    public static final List<String> OTRIGGER_LIST = Arrays.asList(ONBEFORE_CREATED, ONAFTER_CREATED,
            ONBEFORE_DELETE, ONAFTER_DELETE, ONBEFORE_READ, ONAFTER_READ, ONBEFORE_UPDATED, ONAFTER_UPDATED);

    public OTriggerMetaPanel(String componentId, IModel<DisplayMode> modeModel, IModel<OTriggerModel> rowModel, IModel<String> criteryModel) {
        super(componentId, modeModel, rowModel, criteryModel);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected V getValue(OTriggerModel entity, String critery) {
        if(critery.equals(OTriggerModel.TRIGGER)) {
            return (V) entity.getTrigger();
        }
        if(critery.equals(OTriggerModel.FUNCTION)) {
            return (V) entity.getFunction();
        }
        return null;
    }

    @Override
    protected void setValue(OTriggerModel triggerModel, String critery, V value) {
        if (critery.equals(OTriggerModel.FUNCTION)) {
            triggerModel.setFunction((String) value);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Component resolveComponent(String id, DisplayMode mode, String critery) {
        if(DisplayMode.VIEW.equals(mode)) {
            return new Label(id, getModel());
        } else if(DisplayMode.EDIT.equals(mode)) {
            if(critery.equals(OTriggerModel.TRIGGER)) {
                return new Label(id, getModel());
            } else if(critery.equals(OTriggerModel.FUNCTION)) {
                return new Select2Choice<String>(id, (IModel<String>)getModel(), OFunctionTextChoiceProvider.INSTANCE);
            }
        }
        return null;
    }

    @Override
    protected IModel<String> newLabelModel() {
        return new SimpleNamingModel<String>(getPropertyModel().getObject());
    }
}
