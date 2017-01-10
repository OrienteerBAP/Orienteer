package org.orienteer.core.component.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.modal.ImportDialogPanel;
import org.orienteer.core.component.table.OrienteerDataTable;

import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResources;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;

/**
 * {@link Command} to import schema.
 * Additional modal window will be displayed
 */
@RequiredOrientResources({
	@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions={OrientPermission.CREATE, OrientPermission.UPDATE}),
	@RequiredOrientResource(value = OSecurityHelper.SYSTEM_CLUSTERS, permissions={OrientPermission.CREATE, OrientPermission.UPDATE})
})
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
		modal.setContent(new ImportDialogPanel(modal.getContentId(), modal) {

			@Override
			public void onImportFinished(AjaxRequestTarget target) {
				sendActionPerformed();
			}
			
		});
	}
}
