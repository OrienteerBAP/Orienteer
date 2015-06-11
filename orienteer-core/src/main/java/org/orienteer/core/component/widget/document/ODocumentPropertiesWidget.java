package org.orienteer.core.component.widget.document;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.EditODocumentCommand;
import org.orienteer.core.component.command.SaveODocumentCommand;
import org.orienteer.core.component.meta.ODocumentMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Widget to show registered parameters for a document on particular tab
 */
@Widget(defaultDomain="document", id = "parameters", type = ODocument.class)
public class ODocumentPropertiesWidget extends AbstractWidget<ODocument>{
	
	@Inject
	private IOClassIntrospector oClassIntrospector;
	
	private OrienteerStructureTable<ODocument, OProperty> propertiesStructureTable;
	private SaveODocumentCommand saveODocumentCommand;;

	public ODocumentPropertiesWidget(String id, IModel<ODocument> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		Form<ODocument> form = new Form<ODocument>("form", getModel());
		IModel<List<? extends OProperty>> propertiesModel = new LoadableDetachableModel<List<? extends OProperty>>() {
			@Override
			protected List<? extends OProperty> load() {
				return oClassIntrospector.listProperties(getModelObject().getSchemaClass(), getDashboardPanel().getTab(), false);
			}
		};
		propertiesStructureTable = new OrienteerStructureTable<ODocument, OProperty>("properties", getModel(), propertiesModel){

					@Override
					protected Component getValueComponent(String id,
							IModel<OProperty> rowModel) {
						//TODO: remove static displaymode
						return new ODocumentMetaPanel<Object>(id, DisplayMode.VIEW.asModel(), ODocumentPropertiesWidget.this.getModel(), rowModel);
					}
		};
		form.add(propertiesStructureTable);
		add(form);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		//TODO: remove static displaymode
		IModel<DisplayMode> displayMode = DisplayMode.VIEW.asModel();
		propertiesStructureTable.addCommand(new EditODocumentCommand(propertiesStructureTable, displayMode));
		propertiesStructureTable.addCommand(saveODocumentCommand = new SaveODocumentCommand(propertiesStructureTable, displayMode));
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		//TODO: remove static displaymode
		IModel<DisplayMode> displayMode = DisplayMode.VIEW.asModel();
		if(DisplayMode.EDIT.equals(displayMode.getObject()))
		{
			saveODocumentCommand.configure();
			if(!saveODocumentCommand.determineVisibility())
			{
				displayMode.setObject(DisplayMode.VIEW);
			}
		}
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.bars);
	}

	@Override
	protected IModel<String> getTitleModel() {
		return new ResourceModel("wigget.document.properties");
	}

}
