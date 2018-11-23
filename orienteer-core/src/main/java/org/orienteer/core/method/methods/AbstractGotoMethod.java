package org.orienteer.core.method.methods;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.orienteer.core.component.command.Command;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Simple Method to open external resource on button click 
 */
public abstract class AbstractGotoMethod extends AbstractOMethod{

	@Override
	public Command<?> createCommand(String id) {
		return new Command<ODocument>(id, getTitleModel()) {
			
			@Override
			protected void onInitialize() {
				super.onInitialize();
				applyVisualSettings(this);
				applyBehaviors(this);
			}
			
			@Override
			protected AbstractLink newLink(String id) {
				
				return new ExternalLink(id, new StringResourceModel(getUrlResourceKey(), getObjectModelForUrlMapping()));
			}

			@Override
			public void onClick() {
				throw new IllegalStateException("OnClick can't be invoked for bookmarkable links");
			}
		};
	}
	
	protected IModel<?> getObjectModelForUrlMapping() {
		return getContext().getDisplayObjectModel();
	}
	
	protected abstract String getUrlResourceKey();
}
