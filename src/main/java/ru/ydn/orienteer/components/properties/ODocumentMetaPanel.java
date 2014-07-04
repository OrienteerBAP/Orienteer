package ru.ydn.orienteer.components.properties;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Objects;

import ru.ydn.orienteer.components.IMetaComponentResolver;
import ru.ydn.orienteer.model.DynamicPropertyValueModel;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ODocumentMetaPanel<V> extends AbstractMapMetaPanel<ODocument, DisplayMode, OProperty, V> {
	
	
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
				@Override
				public Component resolve(String id, OProperty property) {
					OType oType = property.getType();
					switch(oType)
					{
						case LINK:
							return new LinkViewPanel(id, (IModel<OIdentifiable>)getModel());
						case LINKLIST:
						case LINKSET:
							return new LinksCollectionViewPanel(id, getModel());
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
				@Override
				public Component resolve(String id, OProperty property) {
					OType oType = property.getType();
					switch(oType)
					{
						case BOOLEAN:
							return new BooleanEditPanel(id, (IModel<Boolean>)getModel());
						default:
							return new TextFieldEditPanel<V>(id, getModel()).setType(oType.getDefaultJavaType());
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
