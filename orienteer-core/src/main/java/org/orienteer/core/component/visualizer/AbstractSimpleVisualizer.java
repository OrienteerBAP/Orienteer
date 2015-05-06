package org.orienteer.core.component.visualizer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.wicket.util.lang.Args;

import com.orientechnologies.orient.core.metadata.schema.OType;

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

}
