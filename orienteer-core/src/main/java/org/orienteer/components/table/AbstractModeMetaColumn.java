package org.orienteer.components.table;

import org.apache.wicket.model.IModel;
import org.orienteer.components.properties.AbstractMetaPanel;

public abstract class AbstractModeMetaColumn<T, K, C, S> extends AbstractMetaColumn<T, C, S>
{
	private IModel<K> modeModel;
	
	public AbstractModeMetaColumn(IModel<C> criteryModel, IModel<K> modeModel)
	{
		super(criteryModel);
		this.modeModel = modeModel;
	}

	public AbstractModeMetaColumn(S sortProperty, IModel<C> criteryModel, IModel<K> modeModel)
	{
		super(sortProperty, criteryModel);
		this.modeModel = modeModel;
	}
	
	public IModel<K> getModeModel() {
		return modeModel;
	}
	
	public K getModeObject()
	{
		return getModeModel().getObject();
	}

	@Override
	public void detach() {
		super.detach();
		modeModel.detach();
	}

}
