package org.orienteer.core.component.widget.document;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.OPropertyNameLabel;
import org.orienteer.core.component.command.BookmarkablePageLinkCommand;
import org.orienteer.core.component.command.EditODocumentCommand;
import org.orienteer.core.component.command.SaveODocumentCommand;
import org.orienteer.core.component.meta.ODocumentMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.web.schema.OClassPage;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;

import java.util.List;

/**
 * Widget to show registered parameters for a document on particular tab
 */
@Widget(domain="document", id = ODocumentPropertiesWidget.WIDGET_TYPE_ID, autoEnable=true)
public class ODocumentPropertiesWidget extends AbstractModeAwareWidget<ODocument>{
	
	public static final String WIDGET_TYPE_ID = "parameters";
	
	@Inject
	private IOClassIntrospector oClassIntrospector;
	
	private OrienteerStructureTable<ODocument, OProperty> propertiesStructureTable;
	private SaveODocumentCommand saveODocumentCommand;
	
	public ODocumentPropertiesWidget(String id, IModel<ODocument> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		Form<ODocument> form = new Form<ODocument>("form", getModel());
		IModel<List<OProperty>> propertiesModel = new LoadableDetachableModel<List<OProperty>>() {
			@Override
			protected List<OProperty> load() {
				return oClassIntrospector.listProperties(getModelObject().getSchemaClass(), getDashboardPanel().getTab(), false);
			}
		};
		propertiesStructureTable = new OrienteerStructureTable<ODocument, OProperty>("properties", getModel(), propertiesModel){
			
			@Override
			protected Component getLabelComponent(String id, final IModel<OProperty> rowModel, IModel<?> labelModel) {
				Component ret = new OPropertyNameLabel(id, rowModel);
				ret.add(new AttributeAppender("class", " required"){

					@Override
					public boolean isEnabled(Component component) {
						return DisplayMode.EDIT.equals(getModeObject()) && rowModel.getObject().isNotNull();
					}
					
				});
				return ret;
			}

			@Override
			protected Component getValueComponent(String id,
					IModel<OProperty> rowModel) {
				return new ODocumentMetaPanel<Object>(id, getModeModel(), ODocumentPropertiesWidget.this.getModel(), rowModel);
			}
		};
		form.add(propertiesStructureTable);
		add(form);
		
		addCommand(new BookmarkablePageLinkCommand<ODocument>(newCommandId(), "command.gotoClass", OClassPage.class) {
			@Override
			public PageParameters getPageParameters() {
				return OClassPage.preparePageParameters(ODocumentPropertiesWidget.this.getModelObject().getSchemaClass(), DisplayMode.VIEW);
			}
		});
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		propertiesStructureTable.addCommand(new EditODocumentCommand(propertiesStructureTable, getModeModel()));
		propertiesStructureTable.addCommand(saveODocumentCommand = new SaveODocumentCommand(propertiesStructureTable, getModeModel()));
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		IModel<? extends List<? extends OProperty>> propertiesModel = propertiesStructureTable.getCriteriesModel();
		List<? extends OProperty> properties = propertiesModel.getObject();
		setVisible((properties!=null && !properties.isEmpty()) ||getModel().getObject().getIdentity().isNew());
		if(DisplayMode.EDIT.equals(getModeObject()))
		{
			saveODocumentCommand.configure();
			if(!saveODocumentCommand.determineVisibility())
			{
				setModeObject(DisplayMode.VIEW);
			}
		}
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.bars);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("widget.document.properties");
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}
