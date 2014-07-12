package ru.ydn.orienteer.components.properties;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.components.IMetaComponentResolver;
import ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel;

import com.orientechnologies.orient.core.index.OIndex;

public class OIndexMetaPanel<V> extends AbstractMapMetaPanel<OIndex<?>, DisplayMode, String, V>
{
	
	public OIndexMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<String> criteryModel, IModel<V> model)
	{
		super(id, modeModel, criteryModel, model);
	}

	@Override
	protected IMetaComponentResolver<String> newResolver(DisplayMode key) {
		if(DisplayMode.VIEW.equals(key))
		{
			return new IMetaComponentResolver<String>() {

				private static final long serialVersionUID = 1L;

				@SuppressWarnings("unchecked")
				@Override
				public Component resolve(String id, String critery) {
					return new Label(id, getModel());
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

				private static final long serialVersionUID = 1L;

				@SuppressWarnings("unchecked")
				@Override
				public Component resolve(String id, String critery) {
					return new Label(id, getModel());
				}

				@Override
				public Serializable getSignature(String critery) {
					return critery;
				}
			};
		}
		else return null;
	}

	@Override
	protected IModel<String> newLabelModel() {
		return new AbstractNamingModel<String>(getCriteryModel()) {

			@Override
			public String getResourceKey(String object) {
				return "index."+object;
			}
		};
	}

}
