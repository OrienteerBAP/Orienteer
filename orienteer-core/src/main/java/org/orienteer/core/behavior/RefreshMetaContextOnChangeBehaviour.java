package org.orienteer.core.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.Form;
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.orienteer.core.component.property.IMetaContext;

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
		}
	}

	@Override
	public boolean getDefaultProcessing() {
		return false;
	}

}
