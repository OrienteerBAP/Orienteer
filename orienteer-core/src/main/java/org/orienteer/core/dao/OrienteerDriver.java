package org.orienteer.core.dao;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.transponder.orientdb.ODriver;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class OrienteerDriver extends ODriver {

	public OrienteerDriver() {
	}

	public OrienteerDriver(boolean overrideSchema) {
		super(overrideSchema);
	}
	
	@Override
	public void createType(String typeName, boolean isAbstract, Class<?> mainWrapperClass, String... superTypes) {
		super.createType(typeName, isAbstract, mainWrapperClass, superTypes);
		OrienteerOClass orienteer = mainWrapperClass.getAnnotation(OrienteerOClass.class);
		if(orienteer!=null) {
			OClass createdClass = getSchema().getClass(typeName);
			CustomAttribute.DOMAIN.setValue(createdClass, orienteer.domain());
			if(!Strings.isEmpty(orienteer.nameProperty()))
				CustomAttribute.PROP_NAME.setValue(createdClass, orienteer.nameProperty());
			if(!Strings.isEmpty(orienteer.parentProperty()))
				CustomAttribute.PROP_PARENT.setValue(createdClass, orienteer.parentProperty());
			if(!Strings.isEmpty(orienteer.defaultTab()))
				CustomAttribute.TAB.setValue(createdClass, orienteer.defaultTab());
			if(!Strings.isEmpty(orienteer.sortProperty())) {
				CustomAttribute.SORT_BY.setValue(createdClass, orienteer.sortProperty());
				CustomAttribute.SORT_ORDER.setValue(createdClass, !SortOrder.DESCENDING.equals(orienteer.sortOrder()));
			}
			if(!Strings.isEmpty(orienteer.searchQuery()))
				CustomAttribute.SEARCH_QUERY.setValue(createdClass, orienteer.searchQuery());
			if(!Strings.isEmpty(orienteer.cssClass()))
				CustomAttribute.CSS_CLASS.setValue(createdClass, orienteer.cssClass());
			//TODO: Support displayable! Transponder should invoke some code after creation of all properties
			/*for(String field: orienteer.displayable())
			{
				OProperty oProperty = createdClass.getProperty(field);
				if(oProperty!=null)
				{
					attr.setValue(oProperty, value);
				}
			}
			return
			helper.switchDisplayable(true, orienteer.displayable());*/
		}
	}
	
	@Override
	public void createProperty(String typeName, String propertyName, Type propertyType, String linkedClassName,
			int order, AnnotatedElement annotations) {
		super.createProperty(typeName, propertyName, propertyType, linkedClassName, order, annotations);
		OrienteerOProperty orienteer = annotations.getAnnotation(OrienteerOProperty.class);
		if(orienteer!=null){
			OProperty createdProperty = getSchema().getClass(typeName).getProperty(propertyName);
			OSchemaHelper helper = OSchemaHelper.bind(getSession());
			if(!Strings.isEmpty(orienteer.tab()))
				helper.assignTab(orienteer.tab());
			helper.assignVisualization(orienteer.visualization());
			if(!Strings.isEmpty(orienteer.feature()))
				CustomAttribute.FEATURE.setValue(helper.getOProperty(), orienteer.feature());
			if(!Strings.isEmpty(orienteer.cssClass()))
				CustomAttribute.CSS_CLASS.setValue(helper.getOProperty(), orienteer.cssClass());
			CustomAttribute.UI_READONLY.setValue(helper.getOProperty(), orienteer.uiReadOnly());
			CustomAttribute.DISPLAYABLE.setValue(helper.getOProperty(), orienteer.displayable());
			CustomAttribute.HIDDEN.setValue(helper.getOProperty(), orienteer.hidden());
			if(!Strings.isEmpty(orienteer.script())) {
				helper.calculateBy(orienteer.script());
			} else {
				CustomAttribute.CALCULABLE.setValue(helper.getOProperty(), false);
			}
		}
	}

}
