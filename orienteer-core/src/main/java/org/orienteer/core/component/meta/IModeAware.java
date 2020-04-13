package org.orienteer.core.component.meta;

import org.apache.wicket.model.IModel;

/**
 * Interface for components which support different display modes
 *
 * @param <M> the display mode type
 */
public interface IModeAware<M> {
	
	public IModel<M> getModeModel();
	
	default public M getModeObject() {
		return getModeModel().getObject();
	}
	
	default public IModeAware<M> setModeObject(M mode) {
		getModeModel().setObject(mode);
		return this;
	}
}
