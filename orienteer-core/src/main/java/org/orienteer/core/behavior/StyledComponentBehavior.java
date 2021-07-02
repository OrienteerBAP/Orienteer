package org.orienteer.core.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.core.util.string.interpolator.ConvertingPropertyVariableInterpolator;
import org.apache.wicket.markup.ComponentTag;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.component.table.OPropertyValueColumn;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link Behavior} to style a component if dynamic style has been detected.
 * Cache is enabled by default: it will style all components it binded to per first resolved one.
 * Supports dynamic styling: when CSS class resolved per component model
 */
public class StyledComponentBehavior extends Behavior {
	
	private boolean styleResolved=false;
	private String styleClass=null;
	private boolean styleClassDynamic=false;
	
	protected void resolveStyleClass(Component component) {
		Object modelObject = component.getDefaultModelObject();
		if(modelObject!=null) {
			
			if(modelObject instanceof ODocument) {
				styleClass = CustomAttribute.CSS_CLASS.getValue(((ODocument)modelObject).getSchemaClass());
			} else if(modelObject instanceof OPropertyValueColumn) {
				styleClass = CustomAttribute.CSS_CLASS.getValue(((OPropertyValueColumn)modelObject).getCriteryModel().getObject());
			}
			else if(modelObject instanceof OProperty) {
				styleClass = CustomAttribute.CSS_CLASS.getValue((OProperty)modelObject);
			}
			if(styleClass!=null) {
				styleClassDynamic = styleClass.contains("${");
			}
			styleResolved = true;
		}
	}
	
	@Override
	public void onComponentTag(Component component, ComponentTag tag) {
		if(!styleResolved) resolveStyleClass(component);
		if(styleResolved) {
			String styleClass = this.styleClass;
			if(styleClass!=null) {
				if(styleClassDynamic) {
					styleClass = new ConvertingPropertyVariableInterpolator(styleClass, 
											component.getDefaultModelObject(),
											component,
											component.getLocale()).toString();
				}
				tag.append("class", styleClass, " ");
			}
		}
	}

}
