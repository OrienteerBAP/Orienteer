package org.orienteer.core.widget;

import org.apache.wicket.model.IModel;
import org.orienteer.core.component.meta.IDisplayModeAware;
import org.orienteer.core.component.property.DisplayMode;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Abstract widget class for widgets wich should be aware of page {@link DisplayMode}
 *
 * @param <T> the type of main data object linked to this widget
 */
public abstract class AbstractModeAwareWidget<T> extends AbstractWidget<T> implements IDisplayModeAware {
	
	private IModel<DisplayMode> displayModeModel = DisplayMode.VIEW.asModel();

	public AbstractModeAwareWidget(String id, IModel<T> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		IDisplayModeAware parent = findParent(DashboardPanel.class).findParent(IDisplayModeAware.class);
		if(parent!=null) {
			displayModeModel.setObject(parent.getModeObject());
		}
	}

	@Override
	public IModel<DisplayMode> getModeModel() {
		return displayModeModel;
	}

}
