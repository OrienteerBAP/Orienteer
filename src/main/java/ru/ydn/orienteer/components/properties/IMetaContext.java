package ru.ydn.orienteer.components.properties;

import org.apache.wicket.MarkupContainer;


public interface IMetaContext<C>
{
	public MarkupContainer getContextComponent();
	public <K extends AbstractMetaPanel<?, C, ?>> K getMetaComponent(C critery);
}
