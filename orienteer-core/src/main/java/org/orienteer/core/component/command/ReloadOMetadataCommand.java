package org.orienteer.core.component.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.table.OrienteerDataTable;

import com.orientechnologies.orient.core.metadata.OMetadata;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;

import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResources;

/**
 * {@link Command} to reload {@link OMetadata}/{@link OSchema}
 */
@RequiredOrientResources({
	@RequiredOrientResource(value = OSecurityHelper.DATABASE, permissions=OrientPermission.READ),
	@RequiredOrientResource(value = OSecurityHelper.SYSTEM_CLUSTERS, permissions=OrientPermission.READ),
})
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
	}

}
