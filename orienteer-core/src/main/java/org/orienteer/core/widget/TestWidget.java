package org.orienteer.core.widget;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;

@Widget(id="test", type=String.class)
public class TestWidget extends AbstractWidget<String> {

	private int counter = 0;
	
	public TestWidget(String id, IModel<String> model) {
		super(id, model);
		add(new Label("label", model));
		add(new Link<String>("link") {

			@Override
			public void onClick() {
				counter++;
			}
		}.setBody(new PropertyModel<String>(this, "linkBody")));
	}
	
	public String getLinkBody()
	{
		return "Counter: "+counter;
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.question_circle);
	}

	@Override
	protected IModel<String> getTitleModel() {
		return Model.of("Test widget");
	}

}