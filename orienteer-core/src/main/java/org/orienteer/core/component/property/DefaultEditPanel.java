package org.orienteer.core.component.property;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

	/**
	 *  {@link GenericPanel} to edit some default parameters
	 *
	 * @param <T> editable parameter
	 */
	public class DefaultEditPanel<T> extends GenericPanel<T>
	{
		private static final long serialVersionUID = 1L;
		private static final String TEXTFIELD_ID = "textfield";

		public DefaultEditPanel(String id, IModel<T> model) {
			super(id, model);
			initialize();
		}

		public DefaultEditPanel(String id) {
			super(id);
			initialize();
		}
		
		protected void initialize()
		{
			add(newTextfield(TEXTFIELD_ID));
		}
		
		protected TextField<T> newTextfield(String componentId)
		{
			return new TextField<T>(componentId, getModel());
		}
		
		public TextField<?> getTextfield()
		{
			return (TextField<?>) get(TEXTFIELD_ID);
		}
	}
