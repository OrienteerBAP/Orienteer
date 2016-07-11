package org.orienteer.core.component;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.command.Command;

import ru.ydn.wicket.wicketorientdb.components.RootForm;

/**
 * Panel with Commands on top
 *
 * @param <T> the type of model object
 */
public class AbstractCommandsEnabledPanel<T> extends GenericPanel<T>  implements ICommandsSupportComponent<T> {

	protected final Form<T> form;
	private RepeatingView commands;
	
	
	public AbstractCommandsEnabledPanel(String id) {
		this(id, null);
	}
	
	public AbstractCommandsEnabledPanel(String id, IModel<T> model) {
		super(id, model);
		add(form = newForm("form", model));
		form.add(commands = new RepeatingView("commands"));
	}
	
	protected Form<T> newForm(String id, IModel<T> model) {
		return new Form<>(id, model);
	}

	@Override
	public AbstractCommandsEnabledPanel<T> addCommand(Command<T> command) {
		commands.add(command);
        return this;
	}

	@Override
	public AbstractCommandsEnabledPanel<T> removeCommand(Command<T> command) {
		commands.remove(command);
        return this;
	}

	@Override
	public String newCommandId() {
		return commands.newChildId();
	}
}
