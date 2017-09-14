package org.orienteer.architect.component.panel.command;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.architect.component.panel.IOArchitectOClassesManager;
import org.orienteer.architect.util.JsonUtil;
import org.orienteer.architect.util.OArchitectOClass;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AbstractCheckBoxEnabledCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Add collection with {@link com.orientechnologies.orient.core.metadata.schema.OClass} to OArchitect JavaScript response
 */
public class AddOClassesCommand extends AbstractCheckBoxEnabledCommand<OClass> {

    private final IOArchitectOClassesManager manager;

    public AddOClassesCommand(IModel<String> labelModel, IOArchitectOClassesManager manager) {
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
        List<OArchitectOClass> architectOClasses = toOArchitectOClasses(classes);
        String json = JsonUtil.toJSON(architectOClasses);
        if (Strings.isNullOrEmpty(json)) json = "[]";
        manager.executeCallback(target, json);
        manager.closeModalWindow(target);
    }

    private List<OArchitectOClass> toOArchitectOClasses(List<OClass> classes) {
        List<OArchitectOClass> architectOClasses = new ArrayList<>(classes.size());
        for (OClass oClass : classes) {
            architectOClasses.add(OArchitectOClass.toArchitectOClass(oClass));
        }
        return architectOClasses;
    }

}
