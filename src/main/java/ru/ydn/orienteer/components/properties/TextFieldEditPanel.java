package ru.ydn.orienteer.components.properties;

import org.apache.wicket.core.util.lang.WicketObjects;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidator;

import com.google.common.reflect.TypeToken;

public class TextFieldEditPanel<T> extends GenericPanel<T>
{
	private TextField<T> textField;
	
	public TextFieldEditPanel(String id, IModel<T> model)
	{
		super(id, model);
		initialize();
	}

	public TextFieldEditPanel(String id)
	{
		super(id);
		initialize();
	}
	
	protected void initialize()
	{
		add(textField = newTextField("textField"));
	}
	
	@SuppressWarnings("unchecked")
	protected TextField<T> newTextField(String id)
	{
		return new TextField<T>(id, getModel(), (Class<T>)new TypeToken<T>(getClass()) {}.getRawType());
	}
	
	public TextFieldEditPanel<T> setType(Class<?> type)
	{
		textField.setType(type);
		return this;
	}
	
	public TextFieldEditPanel<T> addValidator(IValidator<T> validator)
	{
		textField.add(validator);
		return this;
	}
	
	public final Class<T> getType()
	{
		return textField.getType();
	}

}
