package org.orienteer.core.component.meta;

import org.apache.wicket.model.IModel;

/**
 * Interface for components which support different display modes
 *
 * @param <M> the display mode type
 */
public interface IModeAware<M> {
	public IModel<M> getModeModel();
	public M getModeObject();
}
