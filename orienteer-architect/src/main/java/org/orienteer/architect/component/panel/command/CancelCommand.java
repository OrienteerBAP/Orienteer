package org.orienteer.architect.component.panel.command;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.architect.component.panel.IOArchitectOClassesManager;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxFormCommand;

/**
 * Cancel command
 */
public class CancelCommand extends AjaxFormCommand<OClass> {

    private final IOArchitectOClassesManager manager;

    public CancelCommand(IModel<String> labelModel, IOArchitectOClassesManager manager) {
        super(labelModel, manager.getTable());
        this.manager = manager;
    }

    @Override
    protected void onInstantiation() {
        super.onInstantiation();
        setBootstrapType(BootstrapType.DANGER);
        setIcon(FAIconType.times);
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        manager.executeCallback(target, "null");
        manager.closeModalWindow(target);
    }

}
