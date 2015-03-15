package org.orienteer.components.commands;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.components.BootstrapType;
import org.orienteer.components.FAIconType;
import org.orienteer.components.commands.modal.ImportDialogPanel;
import org.orienteer.components.table.OrienteerDataTable;

import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;

@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions={OrientPermission.CREATE, OrientPermission.UPDATE})
public class ImportOSchemaCommand extends AbstractModalWindowCommand<OClass>
{
	public ImportOSchemaCommand(OrienteerDataTable<OClass, ?> table)
	{
		super(new ResourceModel("command.import"), table);
		setIcon(FAIconType.upload);
		setBootstrapType(BootstrapType.SUCCESS);
	}

	@Override
	protected void initializeContent(ModalWindow modal) {
		modal.setTitle(new ResourceModel("command.import.modal.title"));
		modal.setContent(new ImportDialogPanel(modal.getContentId(), modal));
	}
}
