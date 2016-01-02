package org.orienteer.core.component.widget.document.security;

import java.util.Arrays;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.AbstractDialog;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.command.AjaxFormCommand;

import ru.ydn.wicket.wicketorientdb.components.RootForm;

import com.orientechnologies.orient.core.metadata.security.ORule;

/**
 * Dialog for entering information about new {@link ORule}
 */
public abstract class AddRuleDialog extends AbstractDialog<Void> {
	
	private IModel<ORule.ResourceGeneric> resourceModel = Model.of();
	private IModel<String> specificModel = Model.of();

	public AddRuleDialog(ModalWindow modal) {
		super(modal);
		form.add(new DropDownChoice<ORule.ResourceGeneric>("resource", resourceModel, 
								Arrays.asList(ORule.ResourceGeneric.values()), new ChoiceRenderer<ORule.ResourceGeneric>("name"))
					.setNullValid(true));
		form.add(new TextField<String>("specific", specificModel));
		
		addCommand(new AjaxFormCommand<Void>(newCommandId(), "command.submit") {
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				onRuleEntered(target, resourceModel.getObject(), specificModel.getObject());
			}
		}.setBootstrapType(BootstrapType.PRIMARY));
	}
	
	protected abstract void onRuleEntered(AjaxRequestTarget target, ORule.ResourceGeneric resource, String specific);
	
}
