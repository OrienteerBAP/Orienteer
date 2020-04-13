package org.orienteer.tours.model;

import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.util.LocalizeFunction;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * Abstract OTourItem to cover tours and steps 
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IOTourItem.OCLASS_NAME, 
		   isAbstract = true,
		   nameProperty = "title")
@XmlAccessorType(XmlAccessType.NONE)
public interface IOTourItem {
	public static final String OCLASS_NAME = "OTourItem";
	
	@DAOField(visualization = UIVisualizersRegistry.VISUALIZER_LOCALIZATION,
			  displayable = true,
			  order = 0)
	public Map<String, String> getTitle();
	
	@XmlElement(name = "title")
	public default String getLocalizedTitle() {
		return LocalizeFunction.getInstance().apply(getTitle());
	}
	
	@DAOField(notNull = true, order = 10, displayable = true)
	@XmlElement
	public String getAlias();
	
	@DAOField(order = 20, displayable = true)
	@XmlElement
	public String getPath();
	
	public default boolean isAllowed() {
		return isAllowed(RequestCycle.get().getRequest().getUrl());
	}
	
	public default boolean isAllowed(Url url) {
		String path = getPath();
		return Strings.isEmpty(path)?true:url.toString().matches(path);
	}
}
