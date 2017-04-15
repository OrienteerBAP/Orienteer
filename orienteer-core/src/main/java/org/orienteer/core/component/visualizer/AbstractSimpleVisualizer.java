package org.orienteer.core.component.visualizer;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Abstract {@link IVisualizer} to simplify stubbing
 */
public abstract class AbstractSimpleVisualizer implements IVisualizer
{
	private final String name;
	private final boolean extended;
	private Collection<OType> supportedTypes;
	
	public AbstractSimpleVisualizer(String name, boolean extended, OType... types)
	{
		this(name, extended, Arrays.asList(types));
	}
	
	public AbstractSimpleVisualizer(String name, boolean extended,
			Collection<OType> supportedTypes)
	{
		Args.notNull(name, "name");
		Args.notNull(supportedTypes, "supportedTypes");
		Args.notEmpty(supportedTypes, "supportedTypes");
		
		this.name = name;
		this.extended = extended;
		this.supportedTypes = Collections.unmodifiableCollection(supportedTypes);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isExtended() {
		return extended;
	}

	@Override
	public Collection<OType> getSupportedTypes() {
		return supportedTypes;
	}

	@Override
	public <V> Component createComponentForFiltering(String id, IModel<OProperty> propertyModel, IModel<V> valueModel) {
		return null;
	}
}
