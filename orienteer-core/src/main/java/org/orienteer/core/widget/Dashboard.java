package org.orienteer.core.widget;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

import com.google.inject.Inject;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

public class Dashboard<T> extends GenericPanel<T> {
	
	private final static WebjarsJavaScriptResourceReference GRIDSTER_JS = new WebjarsJavaScriptResourceReference("/gridster.js/current/jquery.gridster.js");
	private final static WebjarsCssResourceReference GRIDSTER_CSS = new WebjarsCssResourceReference("/gridster.js/current/jquery.gridster.css");
	private final static CssResourceReference WIDGET_CSS = new CssResourceReference(Dashboard.class, "widget.css");
	
	@Inject
	private IWidgetRegistry widgetRegistry;
	
	private RepeatingView repeatingView;
	
	public Dashboard(String id, IModel<T> model) {
		super(id, model);
		repeatingView = new RepeatingView("widgets");
		add(repeatingView);
		setOutputMarkupId(true);
	}
	
	public String newWidgetId()
	{
		return repeatingView.newChildId();
	}
	
	public Dashboard<T> addWidget(AbstractWidget<T> widget)
	{
		repeatingView.add(widget);
		return this;
	}
	
	public Dashboard<T> addWidget(IWidgetDescription<T> description)
	{
		return addWidget(description.instanciate(newWidgetId(), getModel()));
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		int row = 1;
		for(Component child : repeatingView)
		{
			AbstractWidget<?> widget = (AbstractWidget<?>) child;
			widget.configure();
			if(widget.getCol()==null) widget.setCol(1);
			if(widget.getRow()==null) widget.setRow(row++);
		}
	}
	
	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		tag.append("class", "gridster orienteer", " ");
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(GRIDSTER_JS));
		response.render(CssHeaderItem.forReference(GRIDSTER_CSS));
		response.render(CssHeaderItem.forReference(WIDGET_CSS));
		response.render(OnDomReadyHeaderItem.forScript("$('#"+getMarkupId()+"> ul').gridster({widget_margins: [10, 10],widget_base_dimensions: [400, 300]})"));
	}

}
