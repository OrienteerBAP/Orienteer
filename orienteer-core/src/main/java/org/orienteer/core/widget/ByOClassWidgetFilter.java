package org.orienteer.core.widget;

import org.apache.wicket.util.string.Strings;

import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * {@link IWidgetFilter} which use selector with OClass specified
 * @param <T> the type of widget's main object
 */
public abstract class ByOClassWidgetFilter<T> implements IWidgetFilter<T> {

	@Override
	public boolean apply(IWidgetType<T> input) {
		String selector = input.getSelector();
		if(Strings.isEmpty(selector)) return true;
		else {
			OClass oClass = getOClass(); 
			return oClass!=null? oClass.isSubClassOf(selector) : false;
		}
	}
	
	public abstract OClass getOClass();

}
