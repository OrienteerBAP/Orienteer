package org.orienteer.core.component.property;

import java.util.HashMap;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.component.command.CreateODocumentCommand;
import org.orienteer.core.component.command.DeleteODocumentCommand;
import org.orienteer.core.component.command.EditODocumentsCommand;
import org.orienteer.core.component.command.ExportCommand;
import org.orienteer.core.component.command.CopyODocumentCommand;
import org.orienteer.core.component.command.ReleaseODocumentCommand;
import org.orienteer.core.component.command.SaveODocumentsCommand;
import org.orienteer.core.component.command.SelectODocumentCommand;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.service.IOClassIntrospector;

import ru.ydn.wicket.wicketorientdb.behavior.SecurityBehavior;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link GenericPanel} to visualize 'table' for a LINKSET/LINKLIST properties
 */
public class LinksPropertyDataTablePanel extends GenericPanel<ODocument>
{
	
	@Inject
	private IOClassIntrospector oClassIntrospector;
	
	private IModel<OProperty> propertyModel;
	
	public LinksPropertyDataTablePanel(String id, IModel<ODocument> documentModel, OProperty property)
	{
		this(id, documentModel, new OPropertyModel(property));
	}
	
	public LinksPropertyDataTablePanel(String id, IModel<ODocument> documentModel, IModel<OProperty> propertyModel)
	{
		super(id, documentModel);
		this.propertyModel = propertyModel;
		OProperty property = propertyModel.getObject();
		OClass linkedClass = property.getLinkedClass();
		boolean isStructureReadonly = (Boolean)CustomAttribute.CALCULABLE.getValue(property)
									   || (Boolean)CustomAttribute.UI_READONLY.getValue(property)
									   || property.isReadonly();
		IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
		
		ISortableDataProvider<ODocument, String> provider = oClassIntrospector.prepareDataProviderForProperty(property, documentModel);
		OrienteerDataTable<ODocument, String> table = 
				new OrienteerDataTable<ODocument, String>("table", oClassIntrospector.getColumnsFor(linkedClass, true, modeModel), provider, 20);
		final OPropertyNamingModel propertyNamingModel = new OPropertyNamingModel(propertyModel);
		table.setCaptionModel(propertyNamingModel);
		SecurityBehavior securityBehaviour = new SecurityBehavior(documentModel, OrientPermission.UPDATE);
		if(!isStructureReadonly)
		{
			table.addCommand(new CreateODocumentCommand(table, documentModel, propertyModel).add(securityBehaviour));
			table.addCommand(new EditODocumentsCommand(table, modeModel, linkedClass).add(securityBehaviour));
			table.addCommand(new SaveODocumentsCommand(table, modeModel).add(securityBehaviour));
			table.addCommand(new CopyODocumentCommand(table, linkedClass).add(securityBehaviour));
			table.addCommand(new DeleteODocumentCommand(table, linkedClass).add(securityBehaviour));
			table.addCommand(new SelectODocumentCommand(table, documentModel, propertyModel).add(securityBehaviour));
			table.addCommand(new ReleaseODocumentCommand(table, documentModel, propertyModel).add(securityBehaviour));
		}
		else
		{
			table.addCommand(new EditODocumentsCommand(table, modeModel, linkedClass).add(securityBehaviour));
			table.addCommand(new SaveODocumentsCommand(table, modeModel).add(securityBehaviour));
		}
		table.addCommand(new ExportCommand<>(table, new LoadableDetachableModel<String>() {
			@Override
			protected String load() {
				return oClassIntrospector.getDocumentName(LinksPropertyDataTablePanel.this.getModelObject()) +
				"."+propertyNamingModel.getObject();
			}
		}));
		add(table);
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(propertyModel.getObject()!=null && propertyModel.getObject().getLinkedClass()!=null);
	}

}
