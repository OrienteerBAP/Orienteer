package org.orienteer.core.widget.support.gridster;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.util.lang.Args;
import org.orienteer.core.widget.AbstractWidget;

class GridsterWidgetBehaviour extends Behavior {

	private AbstractWidget<?> component;
	
	@Override
	public final void bind(final Component hostComponent)
	{
		Args.notNull(hostComponent, "hostComponent");

		if (component != null)
		{
			throw new IllegalStateException("this kind of handler cannot be attached to " +
				"multiple components; it is already attached to component " + component +
				", but component " + hostComponent + " wants to be attached too");
		}
		if(!(hostComponent instanceof AbstractWidget))
		{
			throw new IllegalStateException("This behaviour can be attached only to "+AbstractWidget.class.getSimpleName()+
																			", but this one: "+hostComponent.getClass().getName());
		}

		component = (AbstractWidget<?>)hostComponent;
	}
	
	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		AbstractWidget<?> widget = (AbstractWidget<?>) component;
		if(widget.getWebRequest().isAjax())
		{
			String script = "$('#"+widget.getDashboardPanel().getMarkupId()+" > ul').data('gridster').ajaxUpdate('"+widget.getMarkupId()+"');";
			response.render(OnDomReadyHeaderItem.forScript(script));
		}
	}
	
	public static GridsterWidgetBehaviour getBehaviour(AbstractWidget<?> widget)
	{
		return widget.getBehaviors(GridsterWidgetBehaviour.class).get(0);
	}
}
