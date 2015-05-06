package org.orienteer.core.component.table;

import org.apache.wicket.model.IModel;
import org.orienteer.core.component.meta.AbstractMetaPanel;

/**
 * {@link AbstractMetaColumn} which take into consideration display mode
 *
 * @param <T> the type of the object that will be rendered in this column's cells
 * @param <K> the type of display mode
 * @param <C> the type of criteria for this column
 * @param <S> the type of the sort property
 */
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
