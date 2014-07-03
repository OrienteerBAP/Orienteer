package ru.ydn.orienteer.components;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import com.google.common.base.Objects;

import ru.ydn.orienteer.components.properties.DisplayMode;

public abstract class MapMetaComponentResolver<C, K> implements IMetaComponentResolver<C>
{
	private Map<K, IMetaComponentResolver<C>> map = new HashMap<K, IMetaComponentResolver<C>>();
	
	public void put(K key, IMetaComponentResolver<C> resolver)
	{
		map.put(key, resolver);
	}

	@Override
	public final Component resolve(String id, C critery) {
		IMetaComponentResolver<C> resolver = map.get(getKey());
		return resolver!=null?resolver.resolve(id, critery):null;
	}
	
	@Override
	public Serializable getSignature(C critery) {
		return Objects.hashCode(getKey(), critery);
	}

	public abstract K getKey();

}
