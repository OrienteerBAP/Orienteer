package ru.ydn.orienteer.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

public class SyncVisibilityBehaviour extends Behavior
{
	private Component sourceComponent;

	public SyncVisibilityBehaviour(Component sourceComponent)
	{
		this.sourceComponent = sourceComponent;
	}

	@Override
	public void onConfigure(Component component) {
		super.onConfigure(component);
		sourceComponent.configure();
		component.setVisible(sourceComponent.determineVisibility());
	}
	
	

	
}
