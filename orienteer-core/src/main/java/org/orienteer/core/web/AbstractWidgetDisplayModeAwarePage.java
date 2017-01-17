package org.orienteer.core.web;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.component.meta.IDisplayModeAware;
import org.orienteer.core.component.property.DisplayMode;

/**
 * {@link AbstractWidgetPage} plus generic stuff for pages which can have a deal with {@link DisplayMode}
 *
 * @param <T> the type of a main object for this page
 */
public abstract class AbstractWidgetDisplayModeAwarePage<T> extends AbstractWidgetPage<T> implements IDisplayModeAware {

	private IModel<DisplayMode> displayModeModel = DisplayMode.VIEW.asModel();
	
	public AbstractWidgetDisplayModeAwarePage() {
		super();
	}

	public AbstractWidgetDisplayModeAwarePage(IModel<T> model) {
		super(model);
	}

	public AbstractWidgetDisplayModeAwarePage(PageParameters parameters) {
		super(parameters);
	}
	
	@Override
	public IModel<DisplayMode> getModeModel() {
		return displayModeModel;
	}
	
	@Override
	public DisplayMode getModeObject() {
		return displayModeModel.getObject();
	}
	
	public AbstractWidgetDisplayModeAwarePage<T> setModeObject(DisplayMode mode) {
		displayModeModel.setObject(mode);
		return this;
	}
}
