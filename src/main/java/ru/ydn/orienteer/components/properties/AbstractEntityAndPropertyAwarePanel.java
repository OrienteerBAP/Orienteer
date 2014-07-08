package ru.ydn.orienteer.components.properties;

import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

@Deprecated
public abstract class AbstractEntityAndPropertyAwarePanel<E, P, V> extends GenericPanel<V> implements IEntityAndPropertyAware<E, P, V>
{
	/**
	 * 
	 */
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
	
	@Override
	public void detachModels() {
		super.detachModels();
		if(entityModel!=null) entityModel.detach();
		if(propertyModel!=null) propertyModel.detach();
	}

}
