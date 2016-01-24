package org.orienteer.core.component.widget.oclass;

import com.google.inject.Inject;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.BookmarkablePageLinkCommand;
import org.orienteer.core.component.command.EditSchemaCommand;
import org.orienteer.core.component.command.SaveSchemaCommand;
import org.orienteer.core.component.meta.OClassMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.widget.document.ODocumentPropertiesWidget;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.web.BrowseOClassPage;
import org.orienteer.core.web.schema.OClassPage;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;

import ru.ydn.wicket.wicketorientdb.components.TransactionlessForm;
import ru.ydn.wicket.wicketorientdb.utils.OSchemaUtils;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Widget to show and modify {@link OClass} configuration
 */
@Widget(id="class-configuration", domain="class", tab="configuration", autoEnable=true)
public class OClassConfigurationWidget extends AbstractModeAwareWidget<OClass> {

	@Inject
	private IOClassIntrospector inspector;

	private OrienteerStructureTable<OClass, String> structureTable;
	
	public OClassConfigurationWidget(String id, IModel<OClass> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		Form<OClass> form = new TransactionlessForm<OClass>("form");
		structureTable  = new OrienteerStructureTable<OClass, String>("attributes", getModel(), OClassMetaPanel.OCLASS_ATTRS) {

			@Override
			protected Component getValueComponent(String id, final IModel<String> rowModel) {
				return new OClassMetaPanel<Object>(id, getModeModel(), OClassConfigurationWidget.this.getModel(), rowModel);
			}
			
		};
		structureTable.addCommand(new EditSchemaCommand<OClass>(structureTable, getModeModel()));
		structureTable.addCommand(new SaveSchemaCommand<OClass>(structureTable, getModeModel(), getModel()));
		
		form.add(structureTable);
		add(form);
		
		addCommand(new BookmarkablePageLinkCommand<OClass>(newCommandId(), "class.browse", BrowseOClassPage.class) {
			@Override
			public PageParameters getPageParameters() {
				return BrowseOClassPage.preparePageParameters(OClassConfigurationWidget.this.getModelObject(), DisplayMode.VIEW);
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(OSchemaUtils.isNotNullOrPrototype(OClassConfigurationWidget.this.getModelObject()));
			}
		});
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.bars);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("class.configuration");
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}
