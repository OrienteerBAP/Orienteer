package org.orienteer.core.component;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Interface to mark components (Widgets) which can be refreshed by themself 
 */
public interface IRefreshable {
	public void refresh(Optional<AjaxRequestTarget> targetOptional);
}
