package org.orienteer.core.component.command;

import org.apache.wicket.extensions.markup.html.repeater.data.table.export.ExportToolbar.DataExportResourceStreamWriter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.IDataExporter;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.IResourceStream;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.ICommandsSupportComponent;
import org.orienteer.core.component.table.OrienteerDataTable;

import com.google.inject.Inject;

/**
 * Command to export data into default format 
 * @param <T> the type of an entity to which this command can be applied
 */
public class ExportCommand <T> extends Command<T> {
	
	@Inject
	private IDataExporter dataExporter;
	
	private final OrienteerDataTable<T, ?> table;
	private final IModel<String> fileNameModel;

	public ExportCommand(OrienteerDataTable<T, ?> table, IModel<String> fileNameModel) {
		super(new ResourceModel("command.export"), table);
		this.table = table;
		this.fileNameModel = fileNameModel;
		setIcon(FAIconType.download);
		setBootstrapType(BootstrapType.PRIMARY);
	}
	
	

	@Override
	public void onClick() {
		//We shouldn't be here
	}
	
	@Override
	protected AbstractLink newLink(String id) {
		IResource resource = new ResourceStreamResource()
		{
			@Override
			protected IResourceStream getResourceStream()
			{
				return new DataExportResourceStreamWriter(dataExporter, table);
			}
		}.setFileName(fileNameModel.getObject() + "." + dataExporter.getFileNameExtension());

		return new ResourceLink<Void>(id, resource);
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		fileNameModel.detach();
	}

}
