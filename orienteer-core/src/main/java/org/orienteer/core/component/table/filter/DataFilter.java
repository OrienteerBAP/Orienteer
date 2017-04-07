package org.orienteer.core.component.table.filter;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * @author Vitaliy Gonchar
 */
public enum DataFilter {
    DOCUMENT("document"),
    CLASS("class"),
    PROPERTY("property"),
    SQL("sql");

    private final String name;

    DataFilter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public IModel<DataFilter> model() {
        return Model.of(this);
    }
}
