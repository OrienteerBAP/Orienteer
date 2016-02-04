package org.orienteer.core.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.orienteer.core.component.meta.IMetaContext;

/**
 * Automatically refresh upper meta context component which affected by changing a component to which this behaviour attached to  
 */
public class RefreshMetaContextOnChangeBehaviour extends AjaxFormSubmitBehavior
{
	public RefreshMetaContextOnChangeBehaviour(Form<?> form)
	{
		super(form, "change");
	}

	public RefreshMetaContextOnChangeBehaviour()
	{
		super("change");
	}
	
	@Override
	protected void onBind() {
		IMetaContext<?> context = AbstractMetaPanel.getMetaContext(getComponent());
		if(context!=null && context instanceof Component)
		{
			((Component)context).setOutputMarkupId(true);
		}
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		IMetaContext<?> context = AbstractMetaPanel.getMetaContext(getComponent());
		if(context!=null)
		{
			target.add(context.getContextComponent());
//			String lastFocusedElementId = target.getLastFocusedElementId();
//			if(!Strings.isEmpty(lastFocusedElementId)) target.appendJavaScript("Wicket.Focus.setFocusOnId(" + lastFocusedElementId + ");");
		}
	}

	@Override
	public boolean getDefaultProcessing() {
		return false;
	}

}
