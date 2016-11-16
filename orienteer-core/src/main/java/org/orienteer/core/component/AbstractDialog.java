package org.orienteer.core.component;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.command.Command;

import ru.ydn.wicket.wicketorientdb.components.RootForm;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Simple Dialog panel
 *
 * @param <T> the type of model object
 */
public class AbstractDialog<T> extends AbstractCommandsEnabledPanel<T>{
	
	protected final ModalWindow modal;
	
	public AbstractDialog(String id) {
		this(id, null);
	}
	
	public AbstractDialog(String id, IModel<T> model) {
		this(id, model, null);
	}
	
	public AbstractDialog(final ModalWindow modal) {
		this(null, modal);
	}
	
	public AbstractDialog(IModel<T> model, final ModalWindow modal) {
		this(modal.getContentId(), model, modal);
	}
	
	public AbstractDialog(String id, IModel<T> model, final ModalWindow modal) {
		super(id, model);
		this.modal = modal;
		if(modal!=null) {
			modal.setMinimalWidth(400);
		}
	}
	
	@Override
	protected Form<T> newForm(String id, IModel<T> model) {
		return new RootForm<T>("form", model);
	}

}
