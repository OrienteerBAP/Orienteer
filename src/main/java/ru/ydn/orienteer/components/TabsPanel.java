package ru.ydn.orienteer.components;

import java.util.List;
import java.util.Objects;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

public class TabsPanel extends GenericPanel<String>
{
	private ListView<String> tabs;
	public TabsPanel(String id, IModel<String> model, List<String> tabs)
	{
		this(id, model, Model.ofList(tabs));
	}
	public TabsPanel(String id, IModel<String> model, IModel<? extends List<? extends String>> tabsModel)
	{
		super(id, model);
		setOutputMarkupPlaceholderTag(true);
		tabs = new ListView<String>("tabs", tabsModel) {

			@Override
			protected void populateItem(final ListItem<String> item) {
				item.add(new AttributeAppender("class", "active")
				{
					@Override
					public boolean isEnabled(Component component) {
						return super.isEnabled(component) && Objects.equals(item.getModelObject(), TabsPanel.this.getModelObject());
					}
					
				});
				
				item.add(new AjaxLink<String>("link", item.getModel()) {
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
	
	protected IModel<String> newTabNameModel(IModel<String> tabModel)
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
	
	

}
