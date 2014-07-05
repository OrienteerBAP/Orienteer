package ru.ydn.orienteer.components.properties;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidator;

import ru.ydn.orienteer.components.IMetaComponentResolver;
import ru.ydn.wicket.wicketorientdb.validation.OSchemaNamesValidator;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class OPropertyMetaPanel<V> extends AbstractMapMetaPanel<OProperty, DisplayMode, String, V>
{
	public OPropertyMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<String> criteryModel, IModel<V> model) {
		super(id, modeModel, criteryModel, model);
	}

	public OPropertyMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<String> criteryModel) {
		super(id, modeModel, criteryModel);
	}

	@Override
	protected IMetaComponentResolver<String> newResolver(DisplayMode key) {
		if(DisplayMode.VIEW.equals(key))
		{
			return new IMetaComponentResolver<String>() {

				@SuppressWarnings("unchecked")
				@Override
				public Component resolve(String id, String critery) {
					if("linkedClass".equals(critery))
					{
						return new OClassViewPanel(id, (IModel<OClass>)getModel());
					}
					else
					{
						return new Label(id, getModel());
					}
				}

				@Override
				public Serializable getSignature(String critery) {
					return critery;
				}
			};
		}
		else if(DisplayMode.EDIT.equals(key))
		{
			return new IMetaComponentResolver<String>() {

				@SuppressWarnings("unchecked")
				@Override
				public Component resolve(String id, String critery) {
					if("name".equals(critery))
					{
						return new TextFieldEditPanel<V>(id, getModel()).addValidator((IValidator<V>)OSchemaNamesValidator.INSTANCE).setType(String.class);
					}
					else if("linkedClass".equals(critery))
					{
						return new OClassViewPanel(id, (IModel<OClass>)getModel());
					}
					else if("mandatory".equals(critery) || "readOnly".equals(critery) || "notNull".equals(critery))
					{
						return new BooleanEditPanel(id, (IModel<Boolean>)getModel());
					}
					else
					{
						return new Label(id, getModel());
					}
				}

				@Override
				public Serializable getSignature(String critery) {
					return critery;
				}
			};
		}
		else return null;
	}
	

}
