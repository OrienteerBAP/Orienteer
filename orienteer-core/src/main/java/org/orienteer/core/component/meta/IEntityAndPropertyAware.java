package org.orienteer.core.component.meta;

import org.apache.wicket.model.IModel;

/**
 * Interface for components that should be aware about entity, property and actual value
 *
 * @param <E> the type of entity
 * @param <P> the type of property
 * @param <V> the type of value
 */
public interface IEntityAndPropertyAware<E, P, V>
{
	public IModel<E> getEntityModel();
	
	public IModel<P> getPropertyModel();
	
	public IModel<V> getValueModel();
}
