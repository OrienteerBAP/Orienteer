package org.orienteer.core.component.command;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.resource.DatabaseExportResource;

import com.orientechnologies.orient.core.db.tool.ODatabaseExport;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

/**
 * {@link Command} to export db schema
 */
@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions=OrientPermission.READ)
public class ExportOSchemaCommand extends Command<OClass>
{

	public ExportOSchemaCommand(OrienteerDataTable<OClass, String> table)
	{
		super(new ResourceModel("command.export"), table);
		setIcon(FAIconType.download);
		setBootstrapType(BootstrapType.SUCCESS);
	}

	@Override
	public void onClick() {
		// NOP
	}

	@Override
	protected AbstractLink newLink(String id) {
		return new ResourceLink<Object>(id, new DatabaseExportResource()
		{

			@Override
			protected ResourceResponse newResourceResponse(Attributes attrs) {
				ResourceResponse resourceResponse = super.newResourceResponse(attrs);
				resourceResponse.setFileName("schema.gz");
				return resourceResponse;
			}

			@Override
			protected void configureODatabaseExport(ODatabaseExport dbExport) {
				dbExport.setOptions("-excludeAll=true -includeSchema=true");
			}
			
		});
	}

}
