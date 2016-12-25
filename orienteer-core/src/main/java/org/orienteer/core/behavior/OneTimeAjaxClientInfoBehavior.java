package org.orienteer.core.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxClientInfoBehavior;
import org.orienteer.core.OrienteerWebSession;

/**
 * Behavior to gather ClientInfo only once 
 */
public class OneTimeAjaxClientInfoBehavior extends AjaxClientInfoBehavior {

	@Override
	public boolean isEnabled(Component component) {
		return !OrienteerWebSession.get().isClientInfoAvailable();
	}
	
	@Override
	public boolean isTemporary(Component component) {
		return !isEnabled(component);
	}
}
