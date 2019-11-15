package org.orienteer.core.component.widget.document;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.interpolator.MapVariableInterpolator;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.IFramePanel;
import org.orienteer.core.component.TabbedPanel;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.model.ODocumentMapWrapper;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Widget to show external views related to current document 
 */
@Widget(id=ExternalViewWidget.EXTERNAL_VIEW_TYPE_ID, domain="document", oClass=ExternalViewWidget.EXTERNAL_VIEW_WIDGET_CLASS)
public class ExternalViewWidget extends AbstractWidget<ODocument> {
	
	public static final String EXTERNAL_VIEW_WIDGET_CLASS = "ExternalViewWidget";
	public static final String EXTERNAL_VIEW_TYPE_ID = "external_view";
	public static final String TABS_PROPERTY_NAME = "tabs";
	
	/**
	 * Implementation of {@link AbstractTab} tp show IFRAMEs 
	 */
	public static class ExternalViewTab extends AbstractTab {
		private IModel<String> externalUrlModel;
		private int height = 500;

		public ExternalViewTab(IModel<String> title, IModel<String> externalUrlModel) {
			super(title);
			this.externalUrlModel = externalUrlModel;
		}
		
		public ExternalViewTab setHeight(int height) {
			this.height = height;
			return this;
		}
		
		public int getHeight() {
			return height;
		}

		@Override
		public WebMarkupContainer getPanel(String containerId) {
			return new IFramePanel(containerId, externalUrlModel).setHeight(getHeight());
		}

	}

	public ExternalViewWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		ODocument widgetDoc = widgetDocumentModel.getObject();
		Map<String, String> tabsMap = widgetDoc.field(TABS_PROPERTY_NAME);
		if(tabsMap==null || tabsMap.isEmpty()) add(new Label("tabs","Configure widget first"));
		else {
			final Integer height = widgetDocumentModel.getObject().field(OWidgetsModule.OPROPERTY_SIZE_Y);
			List<ExternalViewTab> tabsList = new ArrayList<>();
			tabsMap.forEach((key, value) -> {
				tabsList.add(new ExternalViewTab(new SimpleNamingModel<>(key), ()->interpolate(value))
									.setHeight(height!=null?height:500));
			});
			add(new TabbedPanel<>("tabs", tabsList));
		}
	}
	
	

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.video_camera);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("widget.document.externalview");
	}
	
	protected String interpolate(String content) {
		return MapVariableInterpolator.interpolate(content, new ODocumentMapWrapper(getModelObject()));
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}
