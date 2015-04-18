package org.orienteer.core.component.property;

import org.apache.wicket.MarkupContainer;
import org.orienteer.core.component.meta.AbstractMetaPanel;


public interface IMetaContext<C>
{
	public MarkupContainer getContextComponent();
	public <K extends AbstractMetaPanel<?, C, ?>> K getMetaComponent(C critery);
}
