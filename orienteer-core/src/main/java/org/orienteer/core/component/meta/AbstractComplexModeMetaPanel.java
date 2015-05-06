package org.orienteer.core.component.meta;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 * {@link AbstractModeMetaPanel} that can use getValue and setValue methods for dynamic value resolution
 *
 * @param <T> the type of an entity
 * @param <K> the type of a display mode
 * @param <C> the type of a criteria
 * @param <V> the type of a value
 */
public abstract class AbstractComplexModeMetaPanel<T, K, C, V> extends AbstractModeMetaPanel<T, K, C, V> {

	private static final long serialVersionUID = 1L;
	
	public AbstractComplexModeMetaPanel(String id, IModel<K> modeModel,
			IModel<T> entityModel, IModel<C> propertyModel, IModel<V> valueModel)
	{
		super(id, modeModel, entityModel, propertyModel, valueModel);
	}

	public AbstractComplexModeMetaPanel(String id, IModel<K> modeModel, IModel<T> entityModel,
			IModel<C> criteryModel) {
		super(id, modeModel, entityModel, criteryModel);
	}
	
	@Override
	protected IModel<V> resolveValueModel() {
		return new LoadableDetachableModel<V>() {

			private static final long serialVersionUID = 1L;

			@Override
			protected V load() {
				return getValue(getEntityObject(), getPropertyObject());
			}

			@Override
			public void setObject(V object) {
				setValue(getEntityObject(), getPropertyObject(), object);
				super.setObject(object);
			}
			
			
		};
	}

	protected abstract V getValue(T entity, C critery);
	protected abstract void setValue(T entity, C critery, V value);

}
