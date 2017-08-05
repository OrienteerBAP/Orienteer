package org.orienteer.architect.component.panel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.io.IClusterable;
import org.orienteer.architect.util.OArchitectOClass;
import org.orienteer.core.component.table.OrienteerDataTable;

import java.util.List;

/**
 * Interface for manage list of OClasses for add OClasses from schema to orienteer-architect editor
 */
public interface IOArchitectOClassesManager extends IClusterable {
    public void setExistsClasses(List<OArchitectOClass> classes);
    public void executeCallback(AjaxRequestTarget target, String json);
    public void switchModalWindow(AjaxRequestTarget target, boolean show);
    public OrienteerDataTable<OClass, String> getTable();
}
