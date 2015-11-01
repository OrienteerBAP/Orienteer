package org.orienteer.core.component.meta;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.*;
import org.orienteer.core.component.widget.document.OFunctionExecutionModel;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

import java.util.*;

/**
 * Meta panel for {@link com.orientechnologies.orient.core.storage.OCluster}
 *
 * @param <V> type of a value
 */
public class OFunctionExecutionMetaPanel<V> extends AbstractComplexModeMetaPanel<OFunctionExecutionModel, DisplayMode, String, V> implements IDisplayModeAware
{

    public OFunctionExecutionMetaPanel(String id, IModel<DisplayMode> modeModel, IModel<OFunctionExecutionModel> entityModel, IModel<String> criteryModel) {
        super(id, modeModel, entityModel, criteryModel);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected V getValue(OFunctionExecutionModel entity, String critery) {
        if(critery.equals(OFunctionExecutionModel.NAME)) {
            return (V) entity.getName();
        }
        if(critery.equals(OFunctionExecutionModel.PARAMETERS)) {
            return (V) entity.getParameters();
        }
        if(critery.equals(OFunctionExecutionModel.RESULT)) {
            return (V) entity.getResult();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setValue(OFunctionExecutionModel entity, String critery, V value) {
       if(critery.equals(OFunctionExecutionModel.PARAMETERS)) {
           Map<String, String> params = (Map<String, String>) value;
           entity.setParameters(params);
       }
       if(critery.equals(OFunctionExecutionModel.RESULT)) {
           entity.setResult(value.toString());
       }
    }

    @Override
    protected Component resolveComponent(final String id, DisplayMode mode, String critery) {
        if(critery.equals(OFunctionExecutionModel.NAME)) {
            return new Label(id, getModel());
        }

        if(critery.equals(OFunctionExecutionModel.PARAMETERS)) {
            IModel<Map<String, Object>> model = (IModel<Map<String, Object>>) getModel();
            return new FunctionParametersMapPanel<Object>(id,  model);
        }

         if(critery.equals(OFunctionExecutionModel.RESULT)) {
            return new Label(id, getModel());
        }
        return null;
    }

    @Override
    protected IModel<String> newLabelModel() {
        return new SimpleNamingModel<String>("function.execution." + getPropertyModel().getObject());
    }
}
