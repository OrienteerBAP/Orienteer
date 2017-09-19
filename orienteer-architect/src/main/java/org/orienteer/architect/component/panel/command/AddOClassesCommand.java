package org.orienteer.architect.component.panel.command;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.architect.component.panel.IOClassesModalManager;
import org.orienteer.architect.util.JsonUtil;
import org.orienteer.architect.util.OArchitectOClass;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AbstractCheckBoxEnabledCommand;

import java.util.List;

/**
 * Add collection with {@link com.orientechnologies.orient.core.metadata.schema.OClass} to OArchitect JavaScript response
 */
public class AddOClassesCommand extends AbstractCheckBoxEnabledCommand<OClass> {

    private final IOClassesModalManager manager;

    public AddOClassesCommand(IModel<String> labelModel, IOClassesModalManager manager) {
        super(labelModel, manager.getTable());
        this.manager = manager;
    }

    @Override
    protected void onInstantiation() {
        super.onInstantiation();
        setBootstrapType(BootstrapType.PRIMARY);
        setIcon(FAIconType.plus);
    }

    @Override
    protected void performMultiAction(AjaxRequestTarget target, List<OClass> classes) {
        List<OArchitectOClass> architectOClasses = manager.toOArchitectOClasses(classes);
        String json = JsonUtil.toJSON(architectOClasses);
        if (Strings.isNullOrEmpty(json)) json = "[]";
        manager.executeCallback(target, json);
        manager.closeModalWindow(target);
    }
}
