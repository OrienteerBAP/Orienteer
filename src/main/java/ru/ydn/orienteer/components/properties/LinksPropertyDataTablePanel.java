package ru.ydn.orienteer.components.properties;

import java.util.HashMap;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.behavior.SecurityBehavior;
import ru.ydn.orienteer.components.commands.CreateODocumentCommand;
import ru.ydn.orienteer.components.commands.DeleteODocumentCommand;
import ru.ydn.orienteer.components.commands.ReleaseODocumentCommand;
import ru.ydn.orienteer.components.commands.SelectODocumentCommand;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.orienteer.services.IOClassIntrospector;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class LinksPropertyDataTablePanel extends GenericPanel<ODocument>
{
	public static class LinkPropertyDataTablePanelFactory implements UIComponentsRegistry.IUIComponentFactory
	{

		@Override
		public String getName() {
			return "table";
		}

		@Override
		public boolean isExtended() {
			return true;
		}

		@Override
		public Component createComponent(String id, DisplayMode mode,
				IModel<ODocument> documentModel, IModel<OProperty> propertyModel) {
			return new LinksPropertyDataTablePanel(id, documentModel, propertyModel.getObject());
		}

	}
	
	@Inject
	private IOClassIntrospector oClassIntrospector;
	
	public LinksPropertyDataTablePanel(String id, IModel<ODocument> documentModel, OProperty property)
	{
		super(id, documentModel);
		OClass linkedClass = property.getLinkedClass();
		boolean isCalculable = CustomAttributes.CALCULABLE.getValue(property, false);
		OQueryDataProvider<ODocument> provider = oClassIntrospector.prepareDataProviderForProperty(property, documentModel);
		OrienteerDataTable<ODocument, String> table = new OrienteerDataTable<ODocument, String>("table", oClassIntrospector.getColumnsFor(linkedClass, true), provider, 20);
		table.setCaptionModel(new OPropertyNamingModel(property));
		if(!isCalculable)
		{
			OPropertyModel propertyModel = new OPropertyModel(property);
			SecurityBehavior securityBehaviour = new SecurityBehavior(documentModel, OrientPermission.UPDATE);
			table.addCommand(new CreateODocumentCommand(table, documentModel, propertyModel).add(securityBehaviour));
			table.addCommand(new DeleteODocumentCommand(table, linkedClass).add(securityBehaviour));
			table.addCommand(new SelectODocumentCommand(table, documentModel, propertyModel).add(securityBehaviour));
			table.addCommand(new ReleaseODocumentCommand(table, documentModel, propertyModel).add(securityBehaviour));
		}
		add(table);
	}

}
