package org.orienteer.components.commands;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.AbstractResource.ResourceResponse;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.orienteer.components.BootstrapType;
import org.orienteer.components.FAIconType;
import org.orienteer.components.table.OrienteerDataTable;
import org.orienteer.ei.DatabaseExportResource;

import com.orientechnologies.orient.core.db.tool.ODatabaseExport;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;

import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

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
