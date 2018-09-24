package org.orienteer.architect.component.panel.command;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.architect.model.OArchitectOClass;
import org.orienteer.architect.util.JsonUtil;
import org.orienteer.architect.util.OArchitectClassesUtils;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AbstractCheckBoxEnabledCommand;
import org.orienteer.core.component.table.OrienteerDataTable;

import java.util.List;

/**
 * Add collection with {@link com.orientechnologies.orient.core.metadata.schema.OClass} to OArchitect JavaScript response
 */
public class AddOClassesCommand extends AbstractCheckBoxEnabledCommand<OClass> {

    public AddOClassesCommand(IModel<String> labelModel, OrienteerDataTable<OClass, String> table) {
        super(labelModel, table);
    }

    @Override
    protected void onInstantiation() {
        super.onInstantiation();
        setBootstrapType(BootstrapType.PRIMARY);
        setIcon(FAIconType.plus);
    }

    @Override
    protected final void performMultiAction(AjaxRequestTarget target, List<OClass> classes) {
        List<OArchitectOClass> architectOClasses = OArchitectClassesUtils.toOArchitectClasses(classes);
        String json = JsonUtil.toJSON(architectOClasses);
        if (Strings.isNullOrEmpty(json)) {
            json = "[]";
        }
        performAction(target, json);
    }

    protected void performAction(AjaxRequestTarget target, String json) {

    }
}
