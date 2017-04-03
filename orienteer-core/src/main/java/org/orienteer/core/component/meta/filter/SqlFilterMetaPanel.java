package org.orienteer.core.component.meta.filter;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.meta.AbstractComplexModeMetaPanel;
import org.orienteer.core.component.property.DisplayMode;

/**
 * @author Vitaliy Gonchar
 */
public class SqlFilterMetaPanel extends AbstractComplexModeMetaPanel<String, DisplayMode, String, String> {


    public SqlFilterMetaPanel(String id, IModel<DisplayMode> modeModel, IModel<String> entityModel, IModel<String> criteryModel) {
        super(id, modeModel, entityModel, criteryModel);
    }

    @Override
    protected String getValue(String entity, String critery) {
        return entity;
    }

    @Override
    protected void setValue(String entity, String critery, String value) {
        getEntityModel().setObject(value);
    }

    @Override
    protected Component resolveComponent(String id, DisplayMode mode, String critery) {
        switch (mode) {
            case EDIT:
                return new TextField<String>(id, getValueModel());
            case VIEW:
                return new Label(id, getValueModel());
            default:
                return null;
        }
    }

    @Override
    protected IModel<String> newLabelModel() {
        return Model.of("SQL");
    }
}
