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

public class MetaPanel<V> extends AbstractMetaPanel<OProperty, V> {
	private IMetaComponentResolver<OProperty> viewResolver = new IMetaComponentResolver<OProperty>() {
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
	
	private IMetaComponentResolver<OProperty> editResolver = new IMetaComponentResolver<OProperty>() {
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
	
	private IModel<DisplayMode> modeModel;
	
	public MetaPanel(String id, IModel<ODocument> documentModel,
			IModel<OProperty> propertyModel, IModel<DisplayMode> modeModel) {
		super(id, propertyModel, new DynamicPropertyValueModel<V>(documentModel, propertyModel));
		this.modeModel = modeModel;
	}

	
	@Override
	protected Serializable subSign(Serializable thisSignature) {
		return Objects.hashCode(modeModel.getObject(), thisSignature);
	}

	@Override
	public void detachModels() {
		super.detachModels();
		modeModel.detach();
	}


	@Override
	protected IMetaComponentResolver<OProperty> getComponentResolver(OProperty property) {
		DisplayMode mode = modeModel.getObject();
		if(mode.canModify() && property.isReadonly()) mode = DisplayMode.VIEW;
		switch (mode) {
		case EDIT:
			return editResolver;
		case VIEW:
		default:
			return viewResolver;
		}
	}
	
	
}
