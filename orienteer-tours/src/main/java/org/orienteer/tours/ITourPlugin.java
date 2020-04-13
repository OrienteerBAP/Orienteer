package org.orienteer.tours;

import org.apache.wicket.Page;
import org.apache.wicket.markup.head.IHeaderResponse;

import com.google.inject.ImplementedBy;

/**
 * Interface for tour plugins 
 */
@ImplementedBy(DriverJsPlugin.class)
public interface ITourPlugin {
	public void renderHeader(Page page, IHeaderResponse response);
}
