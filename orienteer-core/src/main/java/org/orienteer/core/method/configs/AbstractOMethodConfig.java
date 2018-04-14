package org.orienteer.core.method.configs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.behavior.Behavior;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodConfig;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.IMethodFilter;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.OMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * 
 * {@link OMethod} annotation wrapper
 *
 */
public abstract class AbstractOMethodConfig implements IMethodConfig {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AbstractOMethodConfig.class);
	
	private String methodId;
	private OMethod oMethod;
	private transient List<IMethodFilter> filters;
	private List<Class<? extends Behavior>> behaviors;
	
	public AbstractOMethodConfig(String methodId, OMethod oMethod){
		this.methodId = methodId;
		this.oMethod = oMethod;
		behaviors = Arrays.asList(oMethod.behaviors());
	}
	@Override
	public String titleKey() {
		return oMethod.titleKey();
	}
	@Override
	public FAIconType icon() {
		return oMethod.icon();
	}
	@Override
	public BootstrapType bootstrap() {
		return oMethod.bootstrap();
	}
	@Override
	public boolean changingDisplayMode() {
		return oMethod.changingDisplayMode();
	}
	@Override
	public boolean changingModel() {
		return oMethod.changingModel();
	}
	@Override
	public int order() {
		return oMethod.order();
	}
	
	@Override
	public boolean resetSelection(){
		return oMethod.resetSelection();
	}
	
	@Override
	public String selector() {
		return oMethod.selector();
	}
	@Override
	public String permission() {
		return oMethod.permission();
	}
	@Override
	public Class<? extends IMethod> methodClass() {
		return oMethod.methodClass();
	}
	
	@Override
	public Class<? extends IMethod> oClassTableMethodClass() {
		return oMethod.oClassTableMethodClass();
	}
	
	@Override
	public String getMethodId() {
		return methodId;
	}
	
	@Override
	public List<IMethodFilter> filters() {
		if (filters == null){
			filters = makeFilters(oMethod.filters()); 
		}
		return filters;
	}
	@Override
	public List<Class<? extends Behavior>> behaviors() {
		return behaviors;
	}
	@Override
	public void invokeLinkedFunction(IMethodContext dataObject,ODocument doc) {
		//here we have no linked function		
	}
	
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
