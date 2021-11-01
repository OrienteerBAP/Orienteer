package org.orienteer.tours.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.dao.OrienteerOProperty;
import org.orienteer.transponder.annotation.EntityProperty;
import org.orienteer.transponder.annotation.EntityType;

import com.google.inject.ProvidedBy;

/**
 * DocumentWrapper for tours
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(value = IOTour.OCLASS_NAME, isAbstract = true)
public interface IOTour extends IOTourItem {

	public static final String OCLASS_NAME = "OTour";
	
	@XmlElement
	@OrienteerOProperty(visualization = UIVisualizersRegistry.VISUALIZER_TABLE)
	@EntityProperty(inverse = "tour")
	public List<IOTourStep> getSteps();
}
