package org.orienteer.core.event;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class SwitchDashboardTabEvent {
	protected AjaxRequestTarget target;
	
	public SwitchDashboardTabEvent(AjaxRequestTarget target){
		this.target=target;
	}
	
	public AjaxRequestTarget getTarget(){
		return target;
	}

}
