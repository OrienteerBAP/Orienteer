package org.orienteer.core.behavior;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;

/**
 * {@link Behavior} to adjust attributes on form components to be more compatible with Boostrap/CoreUI 
 */
public class BootstrapFormComponentBehavior extends AjaxFormComponentUpdatingBehavior {
	
	public BootstrapFormComponentBehavior() {
		super("blur");
	}

	private boolean previousValidStatus=false;
	
	@Override
	public void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		FormComponent<?> formComponent = getFormComponent();
		if(formComponent.isRequired()) tag.put("required", (CharSequence)null);
		previousValidStatus = formComponent.isValid();
		if(!previousValidStatus) tag.append("class", "is-invalid", " ");
	}

	@Override
	protected void onUpdate(AjaxRequestTarget target) {
		changeValid(target, true);
	}
	
	@Override
	protected void onError(AjaxRequestTarget target, RuntimeException e) {
		super.onError(target, e);
		changeValid(target, false);
	}
	
	protected void changeValid(AjaxRequestTarget target, boolean valid) {
		if(previousValidStatus!=valid) {
			target.appendJavaScript(String.format("Wicket.DOM.toggleClass('%s', '%s', %s);",
					getFormComponent().getMarkupId(), 
					"is-invalid",
					!valid));
		}
		previousValidStatus = valid;
	}

}
