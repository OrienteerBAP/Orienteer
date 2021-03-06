package org.orienteer.core.component.command;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.ICommandsSupportComponent;
import org.orienteer.core.component.property.DisplayMode;

/**
 * Abstract {@link AjaxFormCommand} for any commands which save something
 *
 * @param <T>  the type of an entity to which this command can be applied
 */
public class AbstractSaveCommand<T> extends AjaxFormCommand<T> {

	private static final long serialVersionUID = 1L;
	private IModel<DisplayMode> displayModeModel;
	
	public AbstractSaveCommand(ICommandsSupportComponent<T> component, IModel<DisplayMode> displayModeModel, IModel<T> model) {
		super(new ResourceModel("command.save"), component, model);
		this.displayModeModel = displayModeModel;
	}

	public AbstractSaveCommand(ICommandsSupportComponent<T> component, IModel<DisplayMode> displayModeModel) {
		super(new ResourceModel("command.save"), component);
		this.displayModeModel = displayModeModel;
	}

	@Override
	protected void onInstantiation() {
		super.onInstantiation();
		setIcon(FAIconType.save);
		setBootstrapType(BootstrapType.PRIMARY);
		setChandingModel(true);
		setChangingDisplayMode(true);
	}

	@Override
	public void onClick(Optional<AjaxRequestTarget> targetOptional) {
		if(displayModeModel!=null) displayModeModel.setObject(DisplayMode.VIEW);
		targetOptional.ifPresent(target -> target.add(this));
	}
	

	@Override
	protected void onConfigure() {
		super.onConfigure();
		if(displayModeModel!=null) setVisible(DisplayMode.EDIT.equals(displayModeModel.getObject()));
	}

	@Override
	public void detachModels() {
		super.detachModels();
		if(displayModeModel!=null) displayModeModel.detach();
	}
	
	public IModel<DisplayMode> getModeModel() {
		return displayModeModel;
	}
	
	public DisplayMode getModeObject() {
		return displayModeModel.getObject();
	}

	public AbstractSaveCommand<T> setModeObject(DisplayMode mode)
	{
		displayModeModel.setObject(mode);
		return this;
	}
	
}
