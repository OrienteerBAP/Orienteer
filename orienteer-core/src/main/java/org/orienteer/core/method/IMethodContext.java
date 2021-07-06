package org.orienteer.core.method;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.model.IModel;
import org.orienteer.core.widget.AbstractWidget;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.ORecordSchemaAware;

import ru.ydn.wicket.wicketorientdb.model.IOClassAware;

/**
 * 
 * Interface for setting method environment data.
 * We recommend using {@link MethodContext} instead your implementation of this interface.  
 *
 */
public interface IMethodContext extends IOClassAware {
	/**
	 * {@link IModel} for current displayed object
	 * @return {@link IModel}
	 */
	public IModel<?> getDisplayObjectModel();
	
	/**
	 * DataSource which is used for data on a component for which commands being rendered
	 * @return
	 */
	public Object getDataSource();
	/**
	 * Current displayed widget
	 * @return current widget
	 */
	public AbstractWidget<?> getCurrentWidget();
	/**
	 * Current displayed widget type
	 * @return widget type
	 */
	public String getCurrentWidgetType();
	/**
	 * Current place
	 * @return {@link MethodPlace}
	 */
	public MethodPlace getPlace();
	/**
	 * Related UI component. Often with internal structure or additional data for {@link getDisplayObjectModel}
	 * Not mandatory
	 * @return table object
	 */
	public Component getRelatedComponent();
	
	@Override
	public default OClass getSchemaClass() {
		Object dataSource = getDataSource();
		if(dataSource instanceof IOClassAware) {
			return ((IOClassAware)dataSource).getSchemaClass();
		}
		if(dataSource instanceof IModel) {
			Object value = ((IModel<?>)dataSource).getObject();
			if(value instanceof OClass) return (OClass) value;
			else if(value instanceof OIdentifiable) {
				ORecord record = ((OIdentifiable)value).getRecord();
				if(record instanceof ORecordSchemaAware) 
					return ((ORecordSchemaAware)record).getSchemaClass();
			}
		}
		return null;
	}
	
	public default void showFeedback(final int feedbackLevel, final String messageKey, final IModel<?> model) {
		AbstractWidget<?> widget = getCurrentWidget();
		if(widget!=null) {
			String message = widget.getLocalizer().getString(messageKey, widget, model);
			widget.getFeedbackMessages().add(new FeedbackMessage(widget, message, feedbackLevel));
			final Page page = widget.findParent(Page.class);
			if (page != null)page.dirty();
		}
	}
	
}
