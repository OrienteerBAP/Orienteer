package ru.ydn.orienteer.components.properties;

import java.io.Serializable;
import java.util.Collection;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.OrienteerWebApplication;
import ru.ydn.orienteer.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ODocumentMetaPanel<V> extends AbstractModeMetaPanel<ODocument, DisplayMode, OProperty, V> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ODocumentMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<ODocument> entityModel, IModel<OProperty> propertyModel,
			IModel<V> valueModel)
	{
		super(id, modeModel, entityModel, propertyModel, valueModel);
	}

	public ODocumentMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<ODocument> entityModel, IModel<OProperty> propertyModel)
	{
		super(id, modeModel, entityModel, propertyModel);
	}
	
	
	@Override
	protected IModel<V> resolveValueModel() {
		return new DynamicPropertyValueModel<V>(getEntityModel(), getPropertyModel());
	}

	@Override
	protected Component resolveComponent(String id, DisplayMode mode,
			OProperty property) {
		if(mode.canModify() && property.isReadonly()) mode = DisplayMode.VIEW;
		OType oType = property.getType();
		if(DisplayMode.VIEW.equals(mode))
		{
			String viewComponent = CustomAttributes.VIEW_COMPONENT.getValue(property);
			if(viewComponent!=null)
			{
				UIComponentsRegistry.IUIComponentFactory factory = OrienteerWebApplication.get().getUIComponentsRegistry().getComponentFactory(mode, oType, viewComponent);
				if(factory!=null) return factory.createComponent(id, getModel());
			}
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
		else if(DisplayMode.EDIT.equals(mode))
		{
			String editComponent = CustomAttributes.EDIT_COMPONENT.getValue(property);
			if(editComponent!=null)
			{
				UIComponentsRegistry.IUIComponentFactory factory = OrienteerWebApplication.get().getUIComponentsRegistry().getComponentFactory(mode, oType, editComponent);
				if(factory!=null) return factory.createComponent(id, getModel());
			}
			switch(oType)
			{
				case BOOLEAN:
					return new CheckBox(id, (IModel<Boolean>)getModel());
				case LINK:
					return new TextField<V>(id, getModel()).setType(ODocument.class);
				default:
					return new TextField<V>(id, getModel()).setType(oType.getDefaultJavaType());
			}
		}
		else return null;
	}

	@Override
	public IModel<String> newLabelModel() {
		return new OPropertyNamingModel(getPropertyModel());
	}
	
}
