package org.orienteer.tours.model;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.dao.OrienteerOClass;
import org.orienteer.core.dao.OrienteerOProperty;
import org.orienteer.core.util.LocalizeFunction;
import org.orienteer.transponder.annotation.EntityProperty;
import org.orienteer.transponder.annotation.EntityType;

import com.google.inject.ProvidedBy;

/**
 * DocumentWrapper for steps 
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(value = IOTourStep.OCLASS_NAME, isAbstract = true)
@OrienteerOClass(parentProperty = "tour")
public interface IOTourStep extends IOTourItem {
	
	public static final String OCLASS_NAME = "OTourStep";	
	
	@EntityProperty(inverse = "steps", order = 30)
	public IOTour getTour();
	
	@EntityProperty(order = 50)
	@OrienteerOProperty(visualization = UIVisualizersRegistry.VISUALIZER_LOCALIZATION)
	public Map<String, String> getContent();
	
	@XmlElement(name="content")
	public default String getLocalizedContent() {
		return LocalizeFunction.getInstance().apply(getContent());
	}
	
	@EntityProperty(order=40)
	@OrienteerOProperty(displayable = true)
	@XmlElement
	public String getElement();
	
}
