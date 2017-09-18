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
public interface IOClassesModalManager extends IClusterable {
    public void setExistsClasses(List<OArchitectOClass> classes);
    public void executeCallback(AjaxRequestTarget target, String json);
    public void showModalWindow(AjaxRequestTarget target);
    public void closeModalWindow(AjaxRequestTarget target);
    public List<OClass> getAllClasses();
    public List<OArchitectOClass> toOArchitectOClasses(List<OClass> classes);
    public OrienteerDataTable<OClass, String> getTable();
}
