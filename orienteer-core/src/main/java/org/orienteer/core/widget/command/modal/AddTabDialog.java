package org.orienteer.core.widget.command.modal;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.orienteer.core.widget.DashboardPanel;


/**
 * Dialog for modal window to add new tab
 *
 * @param <T> the type of main data object for {@link DashboardPanel}
 */
public abstract class AddTabDialog<T> extends Panel {
	
	private TextField<String> tabName;

	public AddTabDialog(String id) {
		super(id);
		Form<T> form = new Form<T>("addTabForm");
		form.add(tabName = new TextField<String>("tabName", Model.of("")));
		form.add(new AjaxButton("addTab") {
			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				onCreateTab(tabName.getModelObject(), Optional.of(target));
				tabName.setModelObject("");
			}
		});
		add(form);
	}
	
	protected abstract void onCreateTab(String name, Optional<AjaxRequestTarget> targetOptional);

}
