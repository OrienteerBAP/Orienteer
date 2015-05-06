package org.orienteer.core.component;

import org.apache.wicket.Component;

/**
 * Interface which shows that a component understands bootstrap specific things: {@link BootstrapType} and {@link BootstrapType}
 */
public interface IBootstrapAware
{
	public Component setBootstrapType(BootstrapType type);
	public BootstrapType getBootstrapType();
	public Component setBootstrapSize(BootstrapSize size);
	public BootstrapSize getBootstrapSize();
}
