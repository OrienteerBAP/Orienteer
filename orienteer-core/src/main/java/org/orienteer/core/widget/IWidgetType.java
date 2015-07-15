package org.orienteer.core.widget;

import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Interface for classes which represent some widget descriptor
 *
 * @param <T> the type of main data
 */
public interface IWidgetType<T> {
	public String getId();
	public String getDomain();
	public String getTab();
	public String getOClassName();
	public int getOrder();
	public Class<? extends AbstractWidget<T>> getWidgetClass();
	public AbstractWidget<T> instanciate(String componentId, IModel<T> model, ODocument widgetDocument);
}
