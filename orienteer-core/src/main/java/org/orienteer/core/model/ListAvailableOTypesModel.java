package org.orienteer.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import ru.ydn.wicket.wicketorientdb.proto.IPrototype;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * Dynamic {@link IModel}&lt;{@link List}&lt; {@link OType} &gt;&gt; 
 * to get {@link OType}s to which current {@link OType} can be switched to 
 */
public class ListAvailableOTypesModel extends LoadableDetachableModel<List<OType>>
{
	private final IModel<OProperty> propertyModel;
	private final static List<OType> WHOLE_LIST = orderTypes(Arrays.asList(OType.values()));
	private final static Map<OType, List<OType>> CACHE_ORDERED = new HashMap<OType, List<OType>>();
	
	public ListAvailableOTypesModel(IModel<OProperty> propertyModel)
	{
		this.propertyModel = propertyModel;
	}
	
	@Override
	protected List<OType> load() {
		OProperty property = propertyModel.getObject();
		
		return property==null || property instanceof IPrototype? findAvailableOTypes(null) : findAvailableOTypes(property.getType());
	}
	
	protected List<OType> findAvailableOTypes(OType type)
	{
		if(type==null) return WHOLE_LIST;
		else
		{
			List<OType> ret = CACHE_ORDERED.get(type);
			if(ret==null)
			{
				List<OType> candidates = new ArrayList<>(Arrays.asList(OType.values()));
				candidates.removeIf(candidate->!candidate.getCastable().contains(type));
				ret = orderTypes(candidates);
				CACHE_ORDERED.put(type, ret);
			}
			return ret;
		}
	}
	
	public static List<OType> orderTypes(Collection<OType> types)
	{
		List<OType> list = types instanceof List?(List<OType>)types:new ArrayList<OType>(types);
		Collections.sort(list, 
				(o1, o2) ->o1.name().compareTo(o2.name()));
		return list;
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		propertyModel.detach();
	}
	
	
	
	
}
