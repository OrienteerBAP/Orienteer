package ru.ydn.orienteer.components.properties.visualizers;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;

import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.model.DynamicPropertyValueModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class SimpleVisualizer implements IVisualizer
{
	private final String name;
	private final boolean extended;
	private final Class<? extends Component> viewComponentClass;
	private final Class<? extends Component> editComponentClass;
	private final Collection<OType> supportedTypes;
	
	public SimpleVisualizer(String name, Class<? extends Component> viewComponentClass, Class<? extends Component> editComponentClass, OType... supportedTypes)
	{
		this(name, viewComponentClass, editComponentClass, Arrays.asList(supportedTypes));
	}
	
	public SimpleVisualizer(String name, Class<? extends Component> viewComponentClass, Class<? extends Component> editComponentClass, Collection<OType> supportedTypes)
	{
		this(name, false, viewComponentClass, editComponentClass, supportedTypes);
	}
	
	public SimpleVisualizer(String name, boolean extended, Class<? extends Component> viewComponentClass, Class<? extends Component> editComponentClass, OType... supportedTypes)
	{
		this(name, extended, viewComponentClass, editComponentClass, Arrays.asList(supportedTypes));
	}
	
	public SimpleVisualizer(String name, boolean extended, Class<? extends Component> viewComponentClass, Class<? extends Component> editComponentClass, Collection<OType> supportedTypes)
	{
		Args.notNull(name, "name");
		Args.notNull(viewComponentClass, "viewComponentClass");
		Args.notNull(editComponentClass, "editComponentClass");
		Args.notNull(supportedTypes, "supportedTypes");
		Args.notEmpty(supportedTypes, "supportedTypes");
		this.name = name;
		this.extended = extended;
		this.viewComponentClass = viewComponentClass;
		this.editComponentClass = editComponentClass;
		this.supportedTypes = supportedTypes;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public boolean isExtended() {
		return extended;
	}

	@Override
	public Component createComponent(String id, DisplayMode mode,
			IModel<ODocument> documentModel, IModel<OProperty> propertyModel) {
		Class<? extends Component> componentClass = DisplayMode.EDIT.equals(mode)?editComponentClass:viewComponentClass;
		try
		{
			Constructor<? extends Component> constructor = componentClass.getConstructor(String.class, IModel.class, IModel.class);
			return constructor.newInstance(id, documentModel, propertyModel);
		} catch (NoSuchMethodException e)
		{
			return createComponent(id, mode, new DynamicPropertyValueModel<ODocument>(documentModel, propertyModel));
		} catch (Exception e)
		{
			throw new WicketRuntimeException("Can't create component", e);
		}
	}

	public <T> Component createComponent(String id, DisplayMode mode,
			IModel<T> model) {
		Class<? extends Component> componentClass = DisplayMode.EDIT.equals(mode)?editComponentClass:viewComponentClass;
		try
		{
			return componentClass.getConstructor(String.class, IModel.class).newInstance(id, model);
		} catch (Exception e)
		{
			throw new WicketRuntimeException("Can't create component", e);
		} 
	}

	@Override
	public Collection<OType> getSupportedTypes() {
		return supportedTypes;
	}

}
