package ru.ydn.orienteer.components.properties;

import java.io.Serializable;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Objects;

import ru.ydn.orienteer.components.IMetaComponentResolver;
import ru.ydn.orienteer.components.MapMetaComponentResolver;

public abstract class AbstractMapMetaPanel<T, K, C, V> extends AbstractMetaPanel<T, C, V> {

	private IModel<K> modeModel;
	private MapMetaComponentResolver<C, K> resolver = new MapMetaComponentResolver<C, K>() {

		@Override
		public K getKey(C critery) {
			return AbstractMapMetaPanel.this.getKey(critery);
		}

		@Override
		protected IMetaComponentResolver<C> newResolver(K key) {
			return AbstractMapMetaPanel.this.newResolver(key);
		}
	};

	public AbstractMapMetaPanel(String id, IModel<K> modeModel, IModel<C> criteryModel,
			IModel<V> model) {
		super(id, criteryModel, model);
		this.modeModel = modeModel;
	}

	public AbstractMapMetaPanel(String id, IModel<K> modeModel, IModel<C> criteryModel) {
		super(id, criteryModel);
		this.modeModel = modeModel;
	}
	
	
	public IModel<K> getModeModel() {
		return modeModel;
	}

	@Override
	protected IMetaComponentResolver<C> getComponentResolver() {
		return resolver;
	}

	@Override
	protected Serializable subSign(Serializable thisSignature) {
		return Objects.hashCode(modeModel.getObject(), thisSignature);
	}
	
	protected K getKey(C critery)
	{
		return modeModel.getObject();
	}
	
	protected abstract IMetaComponentResolver<C> newResolver(K key);

	@Override
	public void detachModels() {
		super.detachModels();
		modeModel.detach();
	}
	
	

}
