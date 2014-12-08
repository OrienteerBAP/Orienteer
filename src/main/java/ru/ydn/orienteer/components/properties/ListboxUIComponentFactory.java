package ru.ydn.orienteer.components.properties;

import java.util.Collection;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.model.DynamicPropertyValueModel;
import ru.ydn.orienteer.schema.SchemaHelper;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.utils.OChoiceRenderer;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ListboxUIComponentFactory implements UIComponentsRegistry.IUIComponentFactory
{

	@Override
	public String getName() {
		return "listbox";
	}

	@Override
	public boolean isExtended() {
		return false;
	}

	@Override
	public Component createComponent(String id, DisplayMode mode,
			IModel<ODocument> documentModel, IModel<OProperty> propertyModel) {
		if(DisplayMode.EDIT.equals(mode))
		{
			
			OProperty property = propertyModel.getObject();
			OClass oClass = property.getLinkedClass();
			OQueryModel<ODocument> choicesModel = new OQueryModel<ODocument>("select from "+oClass.getName()+" LIMIT 100");
			String nameProperty = SchemaHelper.resolveNameProperty(oClass);
			if(property.getType().isMultiValue())
			{
				return new ListMultipleChoice<ODocument>(id, new DynamicPropertyValueModel<Collection<ODocument>>(documentModel, propertyModel), choicesModel, new OChoiceRenderer(nameProperty));
			}
			else
			{
				return new DropDownChoice<ODocument>(id, new DynamicPropertyValueModel<ODocument>(documentModel, propertyModel), 
					choicesModel, new OChoiceRenderer(nameProperty))
					.setNullValid(!property.isNotNull());
			}
		}
		else
		{
			return null;
		}
	}

}
