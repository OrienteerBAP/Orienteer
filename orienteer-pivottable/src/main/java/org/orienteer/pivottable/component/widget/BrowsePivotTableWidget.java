package org.orienteer.pivottable.component.widget;

import static org.orienteer.core.module.OWidgetsModule.OPROPERTY_HIDDEN;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import org.orienteer.core.widget.support.jquery.JQueryDashboardSupport;
import org.orienteer.pivottable.PivotTableModule;
import org.orienteer.pivottable.component.PivotPanel;

import com.google.common.base.Objects;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Widget for free HTML/JS widget for browse page
 */
@Widget(id="pivot-table", domain="browse", oClass=PivotTableModule.WIDGET_OCLASS_NAME, order=10, autoEnable=false)
public class BrowsePivotTableWidget extends AbstractWidget<OClass> {

	private String config;
	
	public BrowsePivotTableWidget(String id, IModel<OClass> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		add(new PivotPanel("pivot", new StringResourceModel("widget.pivottable.urlpattern.oclass", model),
									DisplayMode.EDIT.asModel(),
									new PropertyModel<String>(this, "config")));
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.table);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("widget.pivottable");
	}
	
	@Override
	public void loadSettings() {
		super.loadSettings();
		ODocument doc = getWidgetDocument();
		if(doc==null) return;
		config = doc.field(PivotTableModule.OPROPERTY_PIVOT_TABLE_CONFIG);
	}
	
	@Override
	public void saveSettings() {
		super.saveSettings();
		ODocument doc = getWidgetDocument();
		if(doc==null) return;
		doc.field(PivotTableModule.OPROPERTY_PIVOT_TABLE_CONFIG, config);
	}
	
	public String getConfig() {
		return config;
	}
	
	public void setConfig(String config) {
		this.config = config;
	}

}
