package org.orienteer.core.component.widget.document;

import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.BookmarkablePageLinkCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.event.ActionPerformedEvent;
import org.orienteer.core.web.schema.OClassPage;
import org.orienteer.core.web.schema.OPropertyPage;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;

import ru.ydn.wicket.wicketorientdb.behavior.DisableIfDocumentNotSavedBehavior;
import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * This is temporal widget/adapter between widgets and old approach with {@link IVisualizer}s
 */
@Widget(id = ExtendedVisualizerWidget.WIDGET_TYPE_ID, domain="document")
public class ExtendedVisualizerWidget extends AbstractWidget<ODocument> {
	
	public static final String WIDGET_TYPE_ID = "extended";

	private IModel<OProperty> propertyModel = new OPropertyModel(null);
	
	public ExtendedVisualizerWidget(String id, IModel<ODocument> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		add(DisableIfDocumentNotSavedBehavior.INSTANCE);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		Form<ODocument> form = new Form<ODocument>("form");
		OProperty oProperty = propertyModel.getObject();
		String component = CustomAttribute.VISUALIZATION_TYPE.getValue(oProperty);
		form.add(OrienteerWebApplication.get()
					.getUIVisualizersRegistry()
					.getComponentFactory(oProperty.getType(), component)
					.createComponent("component", DisplayMode.VIEW, getModel(), 
										propertyModel, 
										new DynamicPropertyValueModel<Object>(getModel(), propertyModel)));
		add(form);
		addCommand(new BookmarkablePageLinkCommand<ODocument>(newCommandId(), "command.gotoProperty", OPropertyPage.class) {
			@Override
			public PageParameters getPageParameters() {
				return OPropertyPage.preparePageParameters(propertyModel.getObject(), DisplayMode.VIEW);
			}
		});
		add(UpdateOnActionPerformedEventBehavior.INSTANCE_ALL_CONTINUE);
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.table);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new OPropertyNamingModel(propertyModel);
	}
	
	@Override
	public void saveSettings() {
		super.saveSettings();
		ODocument doc = getWidgetDocument();
		if(doc!=null)
		{
			OProperty property = propertyModel.getObject();
			doc.field("property", property.getName());
		}
	}
	
	@Override
	public void loadSettings() {
		super.loadSettings();
		ODocument doc = getWidgetDocument();
		if(doc!=null)
		{
			String propertyName = doc.field("property");
			OProperty property = getModelObject().getSchemaClass().getProperty(propertyName);
			propertyModel.setObject(property);
		}
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		propertyModel.detach();
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}
	
}
