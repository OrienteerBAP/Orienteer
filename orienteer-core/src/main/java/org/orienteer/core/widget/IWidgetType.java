package org.orienteer.core.widget;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IClusterable;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Interface for classes which represent some widget descriptor
 *
 * @param <T> the type of main data
 */
public interface IWidgetType<T> extends IClusterable {
	public String getId();
	public String getDomain();
	public String getTab();
	public String getOClassName();
	public int getOrder();
	public boolean isAutoEnable();
	public String getSelector();
	public Class<? extends AbstractWidget<T>> getWidgetClass();
	public AbstractWidget<T> instanciate(String componentId, IModel<T> model, ODocument widgetDocument);
}
