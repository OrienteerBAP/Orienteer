package ru.ydn.orienteer.components.properties;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidator;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClass.ATTRIBUTES;

import ru.ydn.orienteer.components.IMetaComponentResolver;
import ru.ydn.wicket.wicketorientdb.validation.OSchemaNamesValidator;

public class OClassMetaPanel<V> extends AbstractMapMetaPanel<OClass, DisplayMode, OClass.ATTRIBUTES, V>
{
	public OClassMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<ATTRIBUTES> criteryModel, IModel<V> model) {
		super(id, modeModel, criteryModel, model);
	}

	public OClassMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<ATTRIBUTES> criteryModel) {
		super(id, modeModel, criteryModel);
	}

	@Override
	protected IMetaComponentResolver<ATTRIBUTES> newResolver(DisplayMode key) {
		if(DisplayMode.VIEW.equals(key))
		{
			return new IMetaComponentResolver<OClass.ATTRIBUTES>() {

				@Override
				public Component resolve(String id, ATTRIBUTES critery) {
					return new Label(id, getModel());
				}

				@Override
				public Serializable getSignature(ATTRIBUTES critery) {
					return critery;
				}
			};
		}
		else if(DisplayMode.EDIT.equals(key))
		{
			return new IMetaComponentResolver<OClass.ATTRIBUTES>() {

				@SuppressWarnings("unchecked")
				@Override
				public Component resolve(String id, ATTRIBUTES critery) {
					switch (critery) {
					case NAME:
					case SHORTNAME:
						return new TextFieldEditPanel<V>(id, getModel()).addValidator((IValidator<V>)OSchemaNamesValidator.INSTANCE).setType(String.class);
					case ABSTRACT:
					case STRICTMODE:
						return new BooleanEditPanel(id, (IModel<Boolean>)getModel());
					default:
						return null;
					}
				}

				@Override
				public Serializable getSignature(ATTRIBUTES critery) {
					return critery;
				}
			};
		}
		else return null;
	}
	

}
