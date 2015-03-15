package org.orienteer.components;

import org.apache.wicket.Component;

public interface IBootstrapAware
{
	public Component setBootstrapType(BootstrapType type);
	public BootstrapType getBootstrapType();
	public Component setBootstrapSize(BootstrapSize size);
	public BootstrapSize getBootstrapSize();
}
