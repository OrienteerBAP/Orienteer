package org.orienteer.core.method.configs;

import java.util.ArrayList;
import java.util.List;

import org.orienteer.core.method.IMethodConfig;
import org.orienteer.core.method.IMethodFilter;
import org.orienteer.core.method.OFilter;

/**
 * 
 * Base method config
 *
 */
public abstract class AbstractOMethodConfig implements IMethodConfig{
	private static final long serialVersionUID = 1L;
	
	protected List<IMethodFilter> makeFilters(OFilter[] filters){
		ArrayList<IMethodFilter> result = new ArrayList<IMethodFilter>(filters.length);
		for (OFilter iMethodFilter : filters) {
			IMethodFilter newFilter;
			try {
				newFilter = iMethodFilter.fClass().newInstance();
				newFilter.setFilterData(iMethodFilter.fData());
				result.add(newFilter);
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

}
