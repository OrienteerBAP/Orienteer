package org.orienteer.tours.model;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.util.LocalizeFunction;

import com.google.inject.ProvidedBy;

/**
 * DocumentWrapper for steps 
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IOTourStep.OCLASS_NAME, isAbstract = true, parentProperty = "tour")
public interface IOTourStep extends IOTourItem {
	
	public static final String OCLASS_NAME = "OTourStep";	
	
	@DAOField(order=30, inverse = "steps")
	public IOTour getTour();
	
	@DAOField(visualization = UIVisualizersRegistry.VISUALIZER_LOCALIZATION, order=50)
	public Map<String, String> getContent();
	
	@XmlElement(name="content")
	public default String getLocalizedContent() {
		return LocalizeFunction.getInstance().apply(getContent());
	}
	
	@DAOField(displayable = true, order=40)
	@XmlElement
	public String getElement();
	
}
