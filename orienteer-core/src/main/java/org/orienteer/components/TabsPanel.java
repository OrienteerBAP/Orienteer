package org.orienteer.components;

import java.util.List;
import java.util.Objects;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

public class TabsPanel<T> extends GenericPanel<T>
{
	private static final CssResourceReference TABDROP_CSS = new CssResourceReference(TabsPanel.class, "tabdrop/tabdrop.css");
	private static final JavaScriptResourceReference TABDROP_JS = new JavaScriptResourceReference(TabsPanel.class, "tabdrop/bootstrap-tabdrop.js");
	
	private ListView<T> tabs;
	public TabsPanel(String id, IModel<T> model, List<T> tabs)
	{
		this(id, model, Model.ofList(tabs));
	}
	public TabsPanel(String id, IModel<T> model, IModel<? extends List<? extends T>> tabsModel)
	{
		super(id, model);
		setOutputMarkupPlaceholderTag(true);
		tabs = new ListView<T>("tabs", tabsModel) {

			@Override
			protected void populateItem(final ListItem<T> item) {
				item.add(new AttributeAppender("class", "active")
				{
					@Override
					public boolean isEnabled(Component component) {
						return super.isEnabled(component) && Objects.equals(item.getModelObject(), TabsPanel.this.getModelObject());
					}

				});
				
				item.add(new AjaxLink<T>("link", item.getModel()) {
					@Override
					public void onClick(AjaxRequestTarget target) {
						TabsPanel.this.setModelObject(item.getModelObject());
						onTabClick(target);
						target.add(TabsPanel.this);
					}
				}.setBody(newTabNameModel(item.getModel())));
			}
		};
		add(tabs);
	}
	
	protected IModel<String> newTabNameModel(IModel<T> tabModel)
	{
		return new SimpleNamingModel(tabModel);
	}
	
	public void onTabClick(AjaxRequestTarget target)
	{
		
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		checkComponentTag(tag, "ul");
		super.onComponentTag(tag);
		tag.append("class", "nav nav-tabs", " ");
		tag.put("role", "tablist");
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(TABDROP_CSS));
		response.render(JavaScriptHeaderItem.forReference(TABDROP_JS));
		response.render(OnDomReadyHeaderItem.forScript("$('#"+getMarkupId()+"').tabdrop({text: '<i class=\"glyphicon glyphicon-align-justify\"></i>'});"));
	}


}
