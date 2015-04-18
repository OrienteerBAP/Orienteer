package org.orienteer.core.component.meta;

import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.IEntityAndPropertyAware;

public abstract class AbstractEntityAndPropertyAwarePanel<E, P, V> extends GenericPanel<V> implements IEntityAndPropertyAware<E, P, V>
{
	private static final long serialVersionUID = 1L;
	private IModel<E> entityModel;
	private IModel<P> propertyModel;
	
	public AbstractEntityAndPropertyAwarePanel(String id, IModel<E> entityModel, IModel<P> propertyModel, IModel<V> valueModel)
	{
		super(id, valueModel);
		this.entityModel = entityModel;
		this.propertyModel = propertyModel;
	}
	
	public AbstractEntityAndPropertyAwarePanel(String id, IModel<E> entityModel, IModel<P> propertyModel)
	{
		super(id);
		this.entityModel = entityModel;
		this.propertyModel = propertyModel;
		setModel(resolveValueModel());
	}
	
	protected abstract IModel<V> resolveValueModel();
	
	public IModel<E> getEntityModel()
	{
		return entityModel;
	}
	
	public IModel<P> getPropertyModel()
	{
		return propertyModel;
	}
	
	public IModel<V> getValueModel()
	{
		return getModel();
	}
	
	public E getEntityObject()
	{
		return getEntityModel().getObject();
	}
	
	public P getPropertyObject()
	{
		return getPropertyModel().getObject();
	}
	
	public V getValueObject()
	{
		return getValueModel().getObject();
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		if(entityModel!=null) entityModel.detach();
		if(propertyModel!=null) propertyModel.detach();
	}

}
