package org.orienteer.tours.model;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.dao.OrienteerOClass;
import org.orienteer.core.dao.OrienteerOProperty;
import org.orienteer.core.util.LocalizeFunction;
import org.orienteer.transponder.annotation.EntityType;
import org.orienteer.transponder.orientdb.OrientDBProperty;

import com.google.inject.ProvidedBy;

/**
 * Abstract OTourItem to cover tours and steps 
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(value = IOTourItem.OCLASS_NAME, 
		   isAbstract = true)
@OrienteerOClass(nameProperty = "title")
@XmlAccessorType(XmlAccessType.NONE)
public interface IOTourItem {
	public static final String OCLASS_NAME = "OTourItem";
	
	@OrienteerOProperty(visualization = UIVisualizersRegistry.VISUALIZER_LOCALIZATION,
			  displayable = true)
	public Map<String, String> getTitle();
	
	@XmlElement(name = "title")
	public default String getLocalizedTitle() {
		return LocalizeFunction.getInstance().apply(getTitle());
	}
	
	@OrienteerOProperty(displayable = true)
	@OrientDBProperty(notNull = true)
	@XmlElement
	public String getAlias();
	
	@OrienteerOProperty(displayable = true)
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
