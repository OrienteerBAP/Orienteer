package org.orienteer.core.component.meta;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.WicketTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.parser.filter.WicketTagIdentifier;
import org.apache.wicket.markup.resolver.IComponentResolver;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.component.property.DisplayMode;

import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link IComponentResolver} to be able to handle wicket:property tags
 */
public class WicketPropertyResolver implements IComponentResolver {
	
	public static final String PROPERTY = "property";
	
	static {
		WicketTagIdentifier.registerWellKnownTagName(PROPERTY);
	}

	@Override
	public Component resolve(MarkupContainer container,
			MarkupStream markupStream, ComponentTag tag) {
		if (tag instanceof WicketTag)
		{
			final WicketTag wTag = (WicketTag)tag;

			// If <wicket:property ...>
			if (PROPERTY.equalsIgnoreCase(wTag.getName()))
			{
				wTag.setAutoComponentTag(false);
				String oClassName = tag.getAttribute("class");
				
				String objectExpression = tag.getAttribute("object");
				IModel<?> model = container.getPage().getDefaultModel();
				if(!Strings.isEmpty(objectExpression)) model = new ODocumentPropertyModel<ODocument>(model, objectExpression);
				
				String property = tag.getAttribute("property");
				if(Strings.isEmpty(property)) property = wTag.getId();
				
				
				if(Strings.isEmpty(oClassName)) {
					Object object = model.getObject();
					if(object instanceof OIdentifiable) {
						ODocument doc = ((OIdentifiable)object).getRecord();
						oClassName = doc.getSchemaClass().getName();
					}
				}
				IModel<OProperty> propertyModel = new OPropertyModel(oClassName, wTag.getId());
				if(propertyModel.getObject()==null) throw new WicketRuntimeException("No such property '"+wTag.getId()+"' defined on class '"+oClassName+"'");
				else return new ODocumentMetaPanel<Object>(wTag.getId(), DisplayMode.VIEW.asModel(), 
										(IModel<ODocument>)model, 
										propertyModel);
			}
		}

		// We were not able to handle the tag
		return null;
	}

}
