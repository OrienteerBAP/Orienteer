package org.orienteer.core.method.configs;

import java.util.ArrayList;
import java.util.List;

import org.orienteer.core.method.IMethodConfig;
import org.orienteer.core.method.IMethodFilter;
import org.orienteer.core.method.OFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Base method config
 *
 */
public abstract class AbstractOMethodConfig implements IMethodConfig{
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AbstractOMethodConfig.class);
	
	protected List<IMethodFilter> makeFilters(OFilter[] filters){
		ArrayList<IMethodFilter> result = new ArrayList<IMethodFilter>(filters.length);
		for (OFilter iMethodFilter : filters) {
			IMethodFilter newFilter;
			try {
				newFilter = iMethodFilter.fClass().newInstance();
				newFilter.setFilterData(iMethodFilter.fData());
				result.add(newFilter);
			} catch (InstantiationException | IllegalAccessException e) {
				LOG.error("Can't make a filter", e);
			}
		}
		return result;
	}

}
