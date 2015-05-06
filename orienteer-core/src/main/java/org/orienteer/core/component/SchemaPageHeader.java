package org.orienteer.core.component;

import java.util.Objects;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.property.AbstractLinkViewPanel;
import org.orienteer.core.web.schema.ListOClassesPage;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

/**
 * Page header for schema specific entities: {@link OClass}, {@link OProperty}, {@link OIndex}
 */
public class SchemaPageHeader extends Panel {
	private RepeatingView childRepeatingView;
	private String lastComponentId;

	public SchemaPageHeader(String id) {
		super(id);
		childRepeatingView = new RepeatingView("child");
		add(childRepeatingView);
		addChild(new AbstractLinkViewPanel<Object>(newChildId()) {

			@Override
			protected AbstractLink newLink(String id) {
				return new BookmarkablePageLink<Object>(id, ListOClassesPage.class)
									.setBody(new ResourceModel("menu.list.class"));
			}
		});
	}
	
	
	public SchemaPageHeader addChild(Component component)
	{
		childRepeatingView.add(component);
		component.add(new AttributeAppender("class", "active")
		{
			@Override
			public boolean isEnabled(Component component) {
				return super.isEnabled(component) && Objects.equals(lastComponentId, component.getId());
			}
		});
		lastComponentId = component.getId();
		return this;
	}
	
	public String newChildId()
	{
		return childRepeatingView.newChildId();
	}
	
}
