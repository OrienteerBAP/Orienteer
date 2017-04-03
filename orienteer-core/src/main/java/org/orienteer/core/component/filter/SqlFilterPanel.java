package org.orienteer.core.component.filter;

import com.google.common.collect.Lists;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.meta.filter.SqlFilterMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;

/**
 * @author Vitaliy Gonchar
 */
public class SqlFilterPanel extends Panel {
    public SqlFilterPanel(String id, final IModel<String> sql, final IModel<DisplayMode> modeModel) {
        super(id);
        add(new OrienteerStructureTable<String, String>("table", sql, Lists.newArrayList("sql")) {
            @Override
            protected Component getValueComponent(String id, IModel<String> rowModel) {
                return new SqlFilterMetaPanel(id, modeModel, sql, rowModel);
            }
        });
    }
}
