package org.orienteer.core.component.command;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;

import com.orientechnologies.orient.core.index.OIndex;

/**
 * {@link Command} to rebuild {@link OIndex}
 */
public class RebuildOIndexCommand extends AjaxCommand<OIndex<?>>
{
	private IModel<OIndex<?>> oIndexModel;

	public RebuildOIndexCommand(OrienteerStructureTable<OIndex<?>, ?> table)
	{
		super(new ResourceModel("command.rebuild"), table);
		this.oIndexModel = table.getModel();
		setBootstrapType(BootstrapType.WARNING);
		setIcon(FAIconType.refresh);
	}

	@Override
	public void onClick(Optional<AjaxRequestTarget> targetOptional) {
		OIndex<?> oIndex = oIndexModel.getObject();
		oIndex.rebuild();
		getPage().success(getLocalizer().getString("success.complete.rebuild", this));
	}

}
