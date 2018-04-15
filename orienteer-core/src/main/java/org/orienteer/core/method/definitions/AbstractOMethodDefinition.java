package org.orienteer.core.method.definitions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.behavior.Behavior;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodDefinition;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.IMethodFilter;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.filters.PermissionFilter;
import org.orienteer.core.method.filters.SelectorFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * 
 * {@link OMethod} annotation wrapper
 *
 */
public abstract class AbstractOMethodDefinition implements IMethodDefinition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AbstractOMethodDefinition.class);
	
	private String methodId;
	private OMethod oMethod;
	private transient List<IMethodFilter> filters;
	private List<Class<? extends Behavior>> behaviors;
	
	public AbstractOMethodDefinition(String methodId, OMethod oMethod){
		this.methodId = methodId;
		this.oMethod = oMethod;
		behaviors = Arrays.asList(oMethod.behaviors());
	}
	@Override
	public String getTitleKey() {
		return oMethod.titleKey();
	}
	@Override
	public FAIconType getIcon() {
		return oMethod.icon();
	}
	@Override
	public BootstrapType getBootstrapType() {
		return oMethod.bootstrap();
	}
	@Override
	public boolean isChangingDisplayMode() {
		return oMethod.changingDisplayMode();
	}
	@Override
	public boolean isChangingModel() {
		return oMethod.changingModel();
	}
	@Override
	public int getOrder() {
		return oMethod.order();
	}
	
	@Override
	public boolean isResetSelection(){
		return oMethod.resetSelection();
	}
	
	@Override
	public String getSelector() {
		return oMethod.selector();
	}
	@Override
	public String getPermission() {
		return oMethod.permission();
	}
	@Override
	public Class<? extends IMethod> getIMethodClass() {
		return oMethod.methodClass();
	}
	
	@Override
	public Class<? extends IMethod> getTableIMethodClass() {
		return oMethod.oClassTableMethodClass();
	}
	
	@Override
	public String getMethodId() {
		return methodId;
	}
	
	@Override
	public List<IMethodFilter> getFilters() {
		if (filters == null){
			filters = makeFilters(oMethod.filters()); 
		}
		return filters;
	}
	@Override
	public List<Class<? extends Behavior>> getBehaviors() {
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
		if (!getSelector().isEmpty()){
			result.add(new SelectorFilter().setFilterData(getSelector()));
		}
		if (!getPermission().isEmpty()){
			result.add(new PermissionFilter().setFilterData(getPermission()));
		}
		return result;
	}
	
	@Override
	public boolean isSupportedMethod(IMethodContext dataObject) {
		if (getFilters()!=null){
			for (IMethodFilter iMethodFilter : getFilters()) {
				if (!iMethodFilter.isSupportedMethod(dataObject)){
					return false;
				}
			}
		}
		return true;
	}
}
