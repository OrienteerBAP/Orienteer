package ru.ydn.orienteer.components.properties;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidator;

import com.orientechnologies.orient.core.metadata.schema.OClass;

import ru.ydn.orienteer.components.IMetaComponentResolver;
import ru.ydn.wicket.wicketorientdb.validation.OSchemaNamesValidator;

public class OClassMetaPanel<V> extends AbstractMapMetaPanel<OClass, DisplayMode, String, V>
{
	public OClassMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<String> criteryModel, IModel<V> model) {
		super(id, modeModel, criteryModel, model);
	}

	public OClassMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<String> criteryModel) {
		super(id, modeModel, criteryModel);
	}

	@Override
	protected IMetaComponentResolver<String> newResolver(DisplayMode key) {
		if(DisplayMode.VIEW.equals(key))
		{
			return new IMetaComponentResolver<String>() {

				@Override
				public Component resolve(String id, String critery) {
					if("clusterSelection".equals(critery))
					{
						return new Label(id, new PropertyModel<String>(getModel(), "name"));
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
					if("name".equals(critery) || "shortName".equals(critery))
					{
						return new TextFieldEditPanel<V>(id, getModel()).addValidator((IValidator<V>)OSchemaNamesValidator.INSTANCE).setType(String.class);
					}
					else if("abstract".equals(critery) || "strictMode".equals(critery))
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
