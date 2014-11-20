package ru.ydn.orienteer.components.commands;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.ResourceModel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

@RequiredOrientResource(value = ORule.ResourceGeneric.DATABASE, permissions=OrientPermission.READ)
public class ReloadOMetadataCommand extends AjaxCommand<OClass>
{

	private static final long serialVersionUID = 1L;

	public ReloadOMetadataCommand(OrienteerDataTable<OClass, ?> table)
	{
		super(new ResourceModel("command.reload"), table);
		setIcon(FAIconType.refresh);
		setBootstrapType(BootstrapType.WARNING);
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		getDatabase().getMetadata().reload();
		send(this, Broadcast.BUBBLE, target);
	}

}
