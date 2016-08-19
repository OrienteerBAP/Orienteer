package org.orienteer.core.component.command;

import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.table.DataTableCommandsToolbar;
import org.orienteer.core.component.table.OrienteerDataTable;

/**
 * Abstract {@link Command} for any commands which copies something after selection
 *
 * @param <T>  the type of an entity to which this command can be applied
 */
public abstract class AbstractCopyCommand<T> extends AbstractCheckBoxEnabledCommand<T> {

    public AbstractCopyCommand(OrienteerDataTable<T, ?> table) {
        super(new ResourceModel("command.copy"), table);
    }

    @Override
    protected void onInstantiation() {
        super.onInstantiation();
        setIcon(FAIconType.copy);
        setBootstrapType(BootstrapType.WARNING);
    }
}
