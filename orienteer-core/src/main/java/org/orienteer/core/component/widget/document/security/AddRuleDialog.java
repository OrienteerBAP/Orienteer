package org.orienteer.core.component.widget.document.security;

import java.util.Arrays;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.orientechnologies.orient.core.metadata.security.ORule;

/**
 * Dialog for entering information about new {@link ORule}
 */
public abstract class AddRuleDialog extends Panel {
	
	private IModel<ORule.ResourceGeneric> resourceModel = Model.of();
	private IModel<String> specificModel = Model.of();

	public AddRuleDialog(String id) {
		super(id);
		Form<Object> form = new Form<Object>("form") {
			@Override
			public Form<?> getRootForm() {
				return this;
			}
		};
		form.add(new DropDownChoice<ORule.ResourceGeneric>("resource", resourceModel, 
								Arrays.asList(ORule.ResourceGeneric.values()), new ChoiceRenderer<ORule.ResourceGeneric>("name"))
					.setNullValid(true));
		form.add(new TextField<String>("specific", specificModel));
		form.add(new AjaxButton("submit", form) {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				onRuleEntered(target, resourceModel.getObject(), specificModel.getObject());
			}
		});
		
		add(form);
	}
	
	protected abstract void onRuleEntered(AjaxRequestTarget target, ORule.ResourceGeneric resource, String specific);
	
}
