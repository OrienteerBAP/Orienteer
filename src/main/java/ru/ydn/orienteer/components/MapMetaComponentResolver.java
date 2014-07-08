package ru.ydn.orienteer.components;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Component;
import com.google.common.base.Objects;

public abstract class MapMetaComponentResolver<C, K> implements IMetaComponentResolver<C>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<K, IMetaComponentResolver<C>> map = new HashMap<K, IMetaComponentResolver<C>>();
	
	public void put(K key, IMetaComponentResolver<C> resolver)
	{
		map.put(key, resolver);
	}

	@Override
	public final Component resolve(String id, C critery) {
		K key = getKey(critery);
		IMetaComponentResolver<C> resolver = map.get(key);
		if(resolver==null)
		{
			resolver = newResolver(key);
			if(resolver!=null) map.put(key, resolver);
		}
		return resolver!=null?resolver.resolve(id, critery):null;
	}
	
	@Override
	public Serializable getSignature(C critery) {
		return Objects.hashCode(getKey(critery), critery);
	}

	public abstract K getKey(C critery);
	
	protected abstract IMetaComponentResolver<C> newResolver(K key);

}
