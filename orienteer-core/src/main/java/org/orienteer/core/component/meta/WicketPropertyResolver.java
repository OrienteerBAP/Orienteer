package org.orienteer.core.component.meta;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.WicketTag;
import org.apache.wicket.markup.parser.filter.WicketTagIdentifier;
import org.apache.wicket.markup.resolver.IComponentResolver;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.orienteer.core.component.property.DisplayMode;

import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.utils.GetODocumentFieldValueFunction;

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
				
				String refComponent = tag.getAttribute("ref");
				IModel<?> model = container.getPage().getDefaultModel();
				if(!Strings.isEmpty(refComponent)) {
					Component component = container;
					if(refComponent.startsWith("/")) { //If starts from / - it means that path is absolute for a page
						component = container.getPage().get(refComponent.substring(1));
					} else if(refComponent.startsWith("#")) {  //Supports format like #element:suba:subb
						int subs = refComponent.indexOf(":");
						final String nameToFind = subs>0?refComponent.substring(1, subs):refComponent.substring(1);
						component = container.getPage().visitChildren((comp, visit) -> {if(comp.getId().equals(nameToFind)) visit.stop(comp);});
						if(subs>0) component = component.get(refComponent.substring(subs+1));
					} else {
						component = container.get(refComponent);
					}
					model = component.getDefaultModel();
				}
				
				String objectExpression = tag.getAttribute("object");
				if(!Strings.isEmpty(objectExpression)) model = new PropertyModel<ODocument>(model, objectExpression);
				
				DisplayMode mode = DisplayMode.parse(tag.getAttribute("mode"), DisplayMode.VIEW);
				String visualization = tag.getAttribute("visualization");
				
				String property = tag.getAttribute("property");
				if(Strings.isEmpty(property)) property = wTag.getId();
				
				
				if(Strings.isEmpty(oClassName)) {
					Object object = model.getObject();
					if(object instanceof OIdentifiable) {
						ODocument doc = ((OIdentifiable)object).getRecord();
						oClassName = doc.getSchemaClass().getName();
					}
				}
				IModel<OProperty> propertyModel = new OPropertyModel(oClassName, property);
				if(propertyModel.getObject()==null) throw new WicketRuntimeException("No such property '"+property+"' defined on class '"+oClassName+"'");
				else return new ODocumentMetaPanel<Object>(wTag.getId(), mode.asModel(), 
										(IModel<ODocument>)model, 
										propertyModel).setVisualization(visualization);
			}
		}

		// We were not able to handle the tag
		return null;
	}

}
