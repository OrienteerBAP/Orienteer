package org.orienteer.core.component.property;

import org.apache.wicket.model.IModel;

public interface IEntityAndPropertyAware<E, P, V>
{
	public IModel<E> getEntityModel();
	
	public IModel<P> getPropertyModel();
	
	public IModel<V> getValueModel();
}
