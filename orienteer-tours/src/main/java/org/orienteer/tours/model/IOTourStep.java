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
	
	@OrienteerOProperty(order=30)
	@EntityProperty(inverse = "steps")
	public IOTour getTour();
	
	@OrienteerOProperty(visualization = UIVisualizersRegistry.VISUALIZER_LOCALIZATION, order=50)
	public Map<String, String> getContent();
	
	@XmlElement(name="content")
	public default String getLocalizedContent() {
		return LocalizeFunction.getInstance().apply(getContent());
	}
	
	@OrienteerOProperty(displayable = true, order=40)
	@XmlElement
	public String getElement();
	
}
