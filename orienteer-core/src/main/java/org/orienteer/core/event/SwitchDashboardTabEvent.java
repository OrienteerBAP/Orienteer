package org.orienteer.core.event;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.cycle.RequestCycle;

/**
 * On switch dashboard tab event
 *
 */
public class SwitchDashboardTabEvent {
	private Optional<AjaxRequestTarget> targetOptional;
	
	public SwitchDashboardTabEvent(Optional<AjaxRequestTarget> targetOptional){
		this.targetOptional=targetOptional;
	}
	
	public Optional<AjaxRequestTarget> getTarget(){
		if(targetOptional==null) {
			targetOptional = RequestCycle.get().find(AjaxRequestTarget.class);
		}
		return targetOptional;
	}

}
