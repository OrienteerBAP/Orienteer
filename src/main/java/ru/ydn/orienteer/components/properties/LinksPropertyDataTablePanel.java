package ru.ydn.orienteer.components.properties;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.components.commands.CreateODocumentCommand;
import ru.ydn.orienteer.components.commands.DeleteODocumentCommand;
import ru.ydn.orienteer.components.commands.ReleaseODocumentCommand;
import ru.ydn.orienteer.components.commands.SelectODocumentCommand;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.orienteer.services.IOClassIntrospector;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

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

		@Override
		public <T> Component createComponent(String id, DisplayMode mode,
				IModel<T> model) {
			throw new WicketRuntimeException("Not supported");
		}
		
	}
	
	@Inject
	private IOClassIntrospector oClassIntrospector;
	
	public LinksPropertyDataTablePanel(String id, IModel<ODocument> documentModel, OProperty property)
	{
		super(id, documentModel);
		String sql;
		OClass linkedClass = property.getLinkedClass();
		boolean isCalculable = CustomAttributes.CALCULABLE.getValue(property, false);
		if(isCalculable)
		{
			sql = CustomAttributes.CALC_SCRIPT.getValue(property);
			sql = sql.replace("?", ":doc");
		}
		else
		{
			sql = "select expand("+property.getName()+") from "+property.getOwnerClass().getName()+" where @rid = :doc";
		}
		OQueryDataProvider<ODocument> provider = new OQueryDataProvider<ODocument>(sql);
		provider.setParameter("doc", documentModel);
		OrienteerDataTable<ODocument, String> table = new OrienteerDataTable<ODocument, String>("table", oClassIntrospector.getColumnsFor(linkedClass, true), provider, 20);
		table.setCaptionModel(new OPropertyNamingModel(property));
		if(!isCalculable)
		{
			OPropertyModel propertyModel = new OPropertyModel(property);
			table.addCommand(new CreateODocumentCommand(table, documentModel, propertyModel));
			table.addCommand(new DeleteODocumentCommand(table, linkedClass));
			table.addCommand(new SelectODocumentCommand(table, documentModel, propertyModel));
			table.addCommand(new ReleaseODocumentCommand(table, documentModel, propertyModel));
		}
		add(table);
	}

}
