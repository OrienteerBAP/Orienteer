package org.orienteer.core.util;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentInitializationListener;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wicket protector: dynamically adjust structure to keep it save 
 */
public class WicketProtector implements IComponentInitializationListener{
	
	private static final Logger LOG = LoggerFactory.getLogger(WicketProtector.class);

	public static final int MAX_INCLUSION = 3;
	@Override
	public void onInitialize(Component component) {
		if(component instanceof AbstractMetaPanel) {
			final AtomicInteger deep = new AtomicInteger(0);
			component.visitParents(AbstractMetaPanel.class, (c, v) -> deep.incrementAndGet());
			if(deep.get()>=MAX_INCLUSION) {
				component.replaceWith(new EmptyPanel(component.getId()));
//				LOG.error("Due to very deep inclusion the following component was replaced by empty panel: "+component);
			}
		}
	}
	
	public static void install(OrienteerWebApplication app) {
		WicketProtector protector = new WicketProtector();
		app.getComponentInitializationListeners().add(protector);
	}

}
