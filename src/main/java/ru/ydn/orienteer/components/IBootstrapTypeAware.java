package ru.ydn.orienteer.components;

import org.apache.wicket.Component;

public interface IBootstrapTypeAware
{
	public Component setBootstrapType(BootstrapType type);
	public BootstrapType getBootstrapType();
}
