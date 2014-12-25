package ru.ydn.orienteer.behavior;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.Form;

import ru.ydn.orienteer.components.properties.AbstractMetaPanel;
import ru.ydn.orienteer.components.properties.IMetaContext;

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
