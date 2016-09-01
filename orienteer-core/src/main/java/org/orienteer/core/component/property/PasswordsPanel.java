package org.orienteer.core.component.property;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.string.Strings;

/**
 * {@link FormComponentPanel} to edit properties with 'password' visualization 
 */
public class PasswordsPanel extends FormComponentPanel<String>
{
	private PasswordTextField password;
	private PasswordTextField confirmPassword;
	public PasswordsPanel(String id, IModel<String> model)
	{
		super(id, model);
		password = new PasswordTextField("password", Model.of(""));
		confirmPassword = new PasswordTextField("confirmPassword", Model.of(""));
		add(password, confirmPassword);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		getForm().add(new EqualPasswordInputValidator(password, confirmPassword));
		IModel<String> labelModel = getLabel();
		password.add(new AttributeModifier("placeholder", new StringResourceModel("password.placeholder.enter", labelModel)));
		password.setLabel(labelModel);
		confirmPassword.add(new AttributeModifier("placeholder", new StringResourceModel("password.placeholder.confirm", labelModel)));
		confirmPassword.setLabel(new StringResourceModel("password.confirm.label", labelModel));
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		boolean shouldBeRequired = isRequired() && Strings.isEmpty(getModelObject());
		password.setRequired(shouldBeRequired);
		confirmPassword.setRequired(shouldBeRequired);
	}

	@Override
	public void convertInput()
 	{
		if(Strings.isEmpty(password.getConvertedInput())){
	 		setConvertedInput(""); //strange,but validator think - "wow, this is not empty value"
		}else{
	 		setConvertedInput(password.getConvertedInput());
		}
 	}


	@Override
	public void updateModel() {
		if(!Strings.isEmpty(getConvertedInput())) super.updateModel();
	}

}
