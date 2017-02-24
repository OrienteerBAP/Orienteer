package org.orienteer.pivottable.component.widget;

import static org.orienteer.core.module.OWidgetsModule.OPROPERTY_HIDDEN;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
import org.orienteer.core.behavior.UpdateOnDashboardDisplayModeChangeBehavior;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.event.ActionPerformedEvent;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import org.orienteer.core.widget.support.jquery.JQueryDashboardSupport;
import org.orienteer.pivottable.PivotTableModule;
import org.orienteer.pivottable.component.PivotPanel;

import com.google.common.base.Objects;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Abstract Widget implementation for Pivot Table
 * @param <T> the type of main data object linked to this widget
 */
public abstract class AbstractPivotTableWidget<T> extends AbstractWidget<T> {

	private String config;
	private String customSQL;
	private double noCacheRnd;

	public AbstractPivotTableWidget(String id, IModel<T> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);

		noCacheRnd = Math.random();

		add(new PivotPanel("pivot", new PropertyModel<String>(this, "url"),
									new PropertyModel<DisplayMode>(this, "displayMode"),
									new PropertyModel<String>(this, "config")));
		add(UpdateOnDashboardDisplayModeChangeBehavior.INSTANCE);
		add(new UpdateOnActionPerformedEventBehavior(false){
			@Override
			protected void update(Component component, ActionPerformedEvent<?> event, IEvent<?> wicketEvent) {
				noCacheRnd = Math.random();
				super.update(component, event, wicketEvent);
			}

			@Override
			protected boolean match(Component component, ActionPerformedEvent<?> event, IEvent<?> wicketEvent) {
				return event.getCommand().isChangingModel();
			}
		});
	}

	public String getUrl() {
		String sql = getSql();
		return "/orientdb/query/db/sql/"+
					UrlEncoder.PATH_INSTANCE.encode(sql, "UTF-8")+
				"/99999?rnd="+
				noCacheRnd;
	}
	
	protected String getSql() {
		String customSql = getCustomSql();
		return Strings.isEmpty(customSQL)?getDefaultSql():customSql;
	}
	
	protected abstract String getDefaultSql();
	
	protected String getCustomSql() {
		return customSQL;
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
		customSQL = doc.field(PivotTableModule.OPROPERTY_PIVOT_CUSTOM_SQL);
	}

	@Override
	public void saveSettings() {
		super.saveSettings();
		ODocument doc = getWidgetDocument();
		if(doc==null) return;
		doc.field(PivotTableModule.OPROPERTY_PIVOT_TABLE_CONFIG, config);
		doc.field(PivotTableModule.OPROPERTY_PIVOT_CUSTOM_SQL, customSQL);
	}

	public DisplayMode getDisplayMode() {
		return getDashboardPanel().getModeObject();
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

}
