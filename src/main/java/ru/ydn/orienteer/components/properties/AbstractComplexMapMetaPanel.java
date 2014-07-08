package ru.ydn.orienteer.components.properties;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public abstract class AbstractComplexMapMetaPanel<T, K, C, V> extends AbstractMapMetaPanel<T, K, C, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IModel<T> entityModel;
	public AbstractComplexMapMetaPanel(String id, IModel<K> modeModel, IModel<T> entityModel,
			IModel<C> criteryModel) {
		super(id, modeModel, criteryModel);
		this.entityModel = entityModel;
		
		setModel(new LoadableDetachableModel<V>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected V load() {
				return getValue(getEntityObject(), getCriteryObject());
			}

			@Override
			public void setObject(V object) {
				setValue(getEntityObject(), getCriteryObject(), object);
				super.setObject(object);
			}
			
			
		});
	}
	
	public IModel<T> getEntityModel() {
		return entityModel;
	}
	
	public T getEntityObject()
	{
		return getEntityModel().getObject();
	}

	protected abstract V getValue(T entity, C critery);
	protected abstract void setValue(T entity, C critery, V value);

}
