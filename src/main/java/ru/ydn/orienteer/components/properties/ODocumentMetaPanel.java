package ru.ydn.orienteer.components.properties;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.validator.RangeValidator;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.OrienteerWebApplication;
import ru.ydn.orienteer.components.properties.visualizers.IVisualizer;
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Component resolveComponent(String id, DisplayMode mode,
			OProperty property) {
		if(mode.canModify() && property.isReadonly()) mode = DisplayMode.VIEW;
		OType oType = property.getType();
		Class<?> javaOType = oType.getDefaultJavaType();
		String visualizationComponent = CustomAttributes.VISUALIZATION_TYPE.getValue(property);
		if(visualizationComponent!=null)
		{
			IVisualizer visualizer = OrienteerWebApplication.get().getUIVisualizersRegistry().getComponentFactory(oType, visualizationComponent);
			if(visualizer!=null) 
			{
				Component ret = visualizer.createComponent(id, mode, getEntityModel(), getPropertyModel());
				if(ret!=null) return ret;
			}
		}
		if(DisplayMode.VIEW.equals(mode))
		{
			switch(oType)
			{
				case LINK:
					return new LinkViewPanel<OIdentifiable>(id, (IModel<OIdentifiable>)getModel());
				case LINKLIST:
				case LINKSET:
					return new LinksCollectionViewPanel<OIdentifiable, Collection<OIdentifiable>>(id, getEntityModel(), property);
                case DATE:
                    return DateLabel.forDatePattern(id, (IModel<Date>) getModel(), ((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, getLocale())).toPattern());
                case DATETIME:
                    return DateLabel.forDatePattern(id, (IModel<Date>) getModel(), ((SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.LONG, getLocale())).toPattern());
                case BOOLEAN:
                	return new BooleanViewPanel(id, (IModel<Boolean>)getModel());
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
				case LINKLIST:
				case LINKSET:
					return new LinksCollectionEditPanel<OIdentifiable, Collection<OIdentifiable>>(id, getEntityModel(), property);
                case DATE:
                    return new DateField(id, (IModel<Date>) getModel());
                case DATETIME:
                    return new DateTimeField(id, (IModel<Date>) getModel());
                default:
                	TextField<V> ret = new TextField<V>(id, getModel());
                	if(javaOType!=null) ret.setType(javaOType);
                	return ret;
			}
		}
		else return null;
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Comparable<? super T> & Serializable> T toRangePoint(String str, Class<?> clazz)
	{
		if(Strings.isEmpty(str)) return null;
		try
		{
			Method method = clazz.getMethod("valueOf", String.class);
			return (T) method.invoke(null, str);
		} catch (Exception e)
		{
			return null;
		} 	
	}

	@Override
	public IModel<String> newLabelModel() {
		return new OPropertyNamingModel(getPropertyModel());
	}
	
}
