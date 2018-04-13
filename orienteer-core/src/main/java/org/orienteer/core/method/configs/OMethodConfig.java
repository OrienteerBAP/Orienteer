package org.orienteer.core.method.configs;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.behavior.Behavior;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.IMethodFilter;
import org.orienteer.core.method.OMethod;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * 
 * {@link OMethod} annotation wrapper
 *
 */
public class OMethodConfig extends AbstractOMethodConfig {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OMethod oMethod;
	private transient List<IMethodFilter> filters;
	private List<Class<? extends Behavior>> behaviors;
	
	public OMethodConfig(OMethod oMethod){
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
}
