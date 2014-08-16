package ru.ydn.orienteer.components.commands;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.structuretable.OrienteerStructureTable;

import com.orientechnologies.orient.core.index.OIndex;

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
	public void onClick(AjaxRequestTarget target) {
		OIndex<?> oIndex = oIndexModel.getObject();
		oIndex.rebuild();
		getPage().success(getLocalizer().getString("success.complete.rebuild", this));
		send(this, Broadcast.BUBBLE, target);
	}

}
