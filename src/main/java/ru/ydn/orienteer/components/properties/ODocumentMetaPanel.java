package ru.ydn.orienteer.components.properties;

import java.io.Serializable;
import java.util.Collection;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.OrienteerWebApplication;
import ru.ydn.orienteer.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;
import ru.ydn.wicket.wicketorientdb.validation.OPropertyValueValidator;

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
	protected void onPostResolveComponent(Component component, OProperty critery) {
		super.onPostResolveComponent(component, critery);
		
		if(component instanceof FormComponent)
		{
			if(critery.isNotNull()) ((FormComponent<?>)component).setRequired(true);
			((FormComponent<?>)component).add(new OPropertyValueValidator<Object>(critery));
		}
	}

	@Override
	protected Component resolveComponent(String id, DisplayMode mode,
			OProperty property) {
		if(mode.canModify() && property.isReadonly()) mode = DisplayMode.VIEW;
		OType oType = property.getType();
		String visualizationComponent = CustomAttributes.VISUALIZATION_TYPE.getValue(property);
		if(visualizationComponent!=null)
		{
			UIComponentsRegistry.IUIComponentFactory factory = OrienteerWebApplication.get().getUIComponentsRegistry().getComponentFactory(oType, visualizationComponent);
			if(factory!=null) 
				return factory.createComponent(id, mode, getEntityModel(), getPropertyModel());
		}
		if(DisplayMode.VIEW.equals(mode))
		{
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
			switch(oType)
			{
				case BOOLEAN:
					return new CheckBox(id, (IModel<Boolean>)getModel());
				case LINK:
					return new LinkEditPanel(id, getEntityModel(), getPropertyModel());
					//return new TextField<V>(id, getModel()).setType(ODocument.class);
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
