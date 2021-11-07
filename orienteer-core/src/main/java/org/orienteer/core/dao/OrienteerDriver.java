package org.orienteer.core.dao;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.transponder.CommonUtils;
import org.orienteer.transponder.orientdb.ODriver;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Orienteer specific Transponder driver
 */
public class OrienteerDriver extends ODriver {

	public OrienteerDriver() {
	}

	public OrienteerDriver(boolean overrideSchema) {
		super(overrideSchema);
	}
	
	@Override
	public void createType(String typeName, boolean isAbstract, Class<?> mainWrapperClass, String... superTypes) {
		super.createType(typeName, isAbstract, mainWrapperClass, superTypes);
		OClass createdClass = getSchema().getClass(typeName);
		CustomAttribute.DAO_CLASS.setValue(createdClass, mainWrapperClass.getName());
		OrienteerOClass orienteer = mainWrapperClass.getAnnotation(OrienteerOClass.class);
		if(orienteer!=null) {
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
		}
	}
	
	@Override
	public void onPostCreateType(String typeName, Class<?> mainWrapperClass) {
		super.onPostCreateType(typeName, mainWrapperClass);
		
		OrienteerOClass orienteer = mainWrapperClass.getAnnotation(OrienteerOClass.class);
		if(orienteer!=null && orienteer.displayable().length>0) {
			OClass createdClass = getSchema().getClass(typeName);
			for (String propertyName : orienteer.displayable()) {
				OProperty property=createdClass.getProperty(propertyName);
				if(property!=null) CustomAttribute.DISPLAYABLE.setValue(property, true);
			}
		}
	}
	
	@Override
	public void createProperty(String typeName, String propertyName, Type propertyType, String linkedClassName,
			int order, AnnotatedElement annotations) {
		super.createProperty(typeName, propertyName, propertyType, linkedClassName, order, annotations);
		OProperty createdProperty = getSchema().getClass(typeName).getProperty(propertyName);
		CustomAttribute.ORDER.setValue(createdProperty, order);
		OrienteerOProperty orienteer = annotations.getAnnotation(OrienteerOProperty.class);
		if(orienteer!=null){
			if(!Strings.isEmpty(orienteer.tab()))
				CustomAttribute.TAB.setValue(createdProperty, orienteer.tab());
			CustomAttribute.VISUALIZATION_TYPE.setValue(createdProperty, orienteer.visualization());
			if(!Strings.isEmpty(orienteer.feature()))
				CustomAttribute.FEATURE.setValue(createdProperty, orienteer.feature());
			if(!Strings.isEmpty(orienteer.cssClass()))
				CustomAttribute.CSS_CLASS.setValue(createdProperty, orienteer.cssClass());
			CustomAttribute.UI_READONLY.setValue(createdProperty, orienteer.uiReadOnly());
			CustomAttribute.DISPLAYABLE.setValue(createdProperty, orienteer.displayable());
			CustomAttribute.HIDDEN.setValue(createdProperty, orienteer.hidden());
			if(!Strings.isEmpty(orienteer.script())) {
				CustomAttribute.CALC_SCRIPT.setValue(createdProperty, orienteer.script());
				CustomAttribute.CALCULABLE.setValue(createdProperty, true);
			} else {
				CustomAttribute.CALCULABLE.setValue(createdProperty, false);
			}
		}
	}
	
	@Override
	public void setupRelationship(String type1Name, String property1Name, String type2Name, String property2Name) {
		super.setupRelationship(type1Name, property1Name, type2Name, property2Name);
		if(property1Name!=null && property2Name!=null)
			OSchemaHelper.bind(getSession())
				.setupRelationship(type1Name, property1Name, type2Name, property2Name);
	}
	
	@Override
	public Class<?> getEntityMainClass(Object seed) {
		if(seed==null) return null;
		Class<?> mainClass = super.getEntityMainClass(seed);
		return mainClass!=null
				?mainClass
				:CommonUtils.safeClassForName(
						CustomAttribute.DAO_CLASS.getValue(((ODocument)((OIdentifiable)seed).getRecord()).getSchemaClass()));
	}

}
