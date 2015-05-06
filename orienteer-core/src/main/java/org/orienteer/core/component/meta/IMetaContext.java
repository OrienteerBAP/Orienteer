package org.orienteer.core.component.meta;

import org.apache.wicket.MarkupContainer;


/**
 * Interface to mark components that reflect meta context
 *
 * @param <C> the type of criterias 
 */
public interface IMetaContext<C>
{
	public MarkupContainer getContextComponent();
	public <K extends AbstractMetaPanel<?, C, ?>> K getMetaComponent(C critery);
}
