package org.orienteer.core.component.meta;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.lang.Objects;

/**
 * {@link AbstractMetaPanel} that can additionally take into consideration display mode.
 *
 * @param <T> the type of an entity
 * @param <K> the type of a display mode
 * @param <C> the type of a criteria
 * @param <V> the type of a value
 */
public abstract class AbstractModeMetaPanel<T, K, C, V> extends AbstractMetaPanel<T, C, V> implements IModeAware<K> {

	private static final long serialVersionUID = 1L;
	private IModel<K> modeModel;
	
	public AbstractModeMetaPanel(String id, IModel<K> modeModel, IModel<T> entityModel,
			IModel<C> propertyModel, IModel<V> valueModel)
	{
		super(id, entityModel, propertyModel, valueModel);
		this.modeModel = modeModel;
	}

	public AbstractModeMetaPanel(String id, IModel<K> modeModel, IModel<T> entityModel,
			IModel<C> propertyModel)
	{
		super(id, entityModel, propertyModel);
		this.modeModel = modeModel;
	}

	@Override
	public IModel<K> getModeModel() {
		return modeModel;
	}

	@Override
	protected Component resolveComponent(String id, C critery) {
		K mode = getModeObject();
		Args.notNull(mode, "mode");
		return resolveComponent(id, getEffectiveMode(mode, critery), critery);
	}
	
	protected K getEffectiveMode(K mode, C critery) {
		return mode;
	}
	
	@Override
	protected Serializable getSignature(C critery) {
		return Objects.hashCode(critery, getModeObject());
	}

	protected abstract Component resolveComponent(String id, K mode, C critery);

	@Override
	public void detachModels() {
		super.detachModels();
		modeModel.detach();
	}
	
}
