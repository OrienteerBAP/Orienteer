package org.orienteer.core.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Panel to show IFRAME 
 */
public class IFramePanel extends GenericPanel<String> {
	
	private int height=300;

	public IFramePanel(String id, IModel<String> model) {
		super(id, model);
		add(new WebMarkupContainer("iframe")
				.add(new AttributeModifier("src", model),
					 new AttributeAppender("style", () -> "height: "+height+"px", ";")));
		
	}
	
	public int getHeight() {
		return height;
	}
	
	public IFramePanel setHeight(int height) {
		this.height = height;
		return this;
	}

}
