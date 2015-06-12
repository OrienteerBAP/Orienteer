package org.orienteer.core.component.widget;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Test widget - just for implementation period
 */
@Widget(id="test", type=String.class, defaultDomain="test"/*, oClass="TestWidget"*/)
public class TestWidget extends AbstractWidget<String> {

	public TestWidget(String id, IModel<String> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		add(new Label("label", model));
		add(new Link<String>("link") {

			@Override
			public void onClick() {
				incCounter();
			}
		}.setBody(new PropertyModel<String>(this, "linkBody")));
		add(new AjaxLink<String>("ajaxLink") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				target.add(TestWidget.this);
			}
		});
	}
	
	public String getLinkBody()
	{
		return "Counter: "+getCounter();
	}
	
	public int getCounter() {
		Integer counter = getWidgetDocument().field("counter");
		return counter!=null?counter:0;
	}
	
	public void setCounter(int counter) {
		getWidgetDocument().field("counter", counter);
	}
	
	public void incCounter() {
		setCounter(getCounter()+1);
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
