package ru.ydn.orienteer.components.properties;

import java.io.Serializable;
import java.util.Collection;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.components.IMetaComponentResolver;
import ru.ydn.orienteer.model.DynamicPropertyValueModel;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ODocumentMetaPanel<V> extends AbstractMapMetaPanel<ODocument, DisplayMode, OProperty, V> {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ODocumentMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<ODocument> documentModel, IModel<OProperty> propertyModel) {
		super(id, modeModel, propertyModel, new DynamicPropertyValueModel<V>(documentModel, propertyModel));
	}
	
	public ODocumentMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<OProperty> criteryModel) {
		super(id, modeModel, criteryModel);
	}




	@Override
	protected DisplayMode getKey(OProperty property) {
		DisplayMode mode = super.getKey(property);
		if(mode.canModify() && property.isReadonly()) mode = DisplayMode.VIEW;
		return mode;
	}

	@Override
	protected IMetaComponentResolver<OProperty> newResolver(DisplayMode key) {
		if(DisplayMode.VIEW.equals(key))
		{
			return new IMetaComponentResolver<OProperty>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@SuppressWarnings("unchecked")
				@Override
				public Component resolve(String id, OProperty property) {
					OType oType = property.getType();
					switch(oType)
					{
						case LINK:
							return new LinkViewPanel<OIdentifiable>(id, (IModel<OIdentifiable>)getModel());
						case LINKLIST:
						case LINKSET:
							return new LinksCollectionViewPanel<OIdentifiable, Collection<OIdentifiable>>(id, (IModel<Collection<OIdentifiable>>)getModel());
						default:
							return new Label(id, getModel());
					}
				}

				@Override
				public Serializable getSignature(OProperty critery) {
					return critery.getFullName();
				}
			};
		}
		else if(DisplayMode.EDIT.equals(key))
		{
			return new IMetaComponentResolver<OProperty>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@SuppressWarnings("unchecked")
				@Override
				public Component resolve(String id, OProperty property) {
					OType oType = property.getType();
					switch(oType)
					{
						case BOOLEAN:
							return new CheckBox(id, (IModel<Boolean>)getModel());
						default:
							return new TextField<V>(id, getModel()).setType(oType.getDefaultJavaType());
					}
				}

				@Override
				public Serializable getSignature(OProperty critery) {
					return critery.getFullName();
				}
			};
		}
		else return null;
	}
	
}
