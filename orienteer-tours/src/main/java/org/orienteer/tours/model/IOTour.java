package org.orienteer.tours.model;

import static org.orienteer.core.util.CommonUtils.mapIdentifiables;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.ODocumentWrapperProvider;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * DocumentWrapper for tours
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IOTour.OCLASS_NAME, isAbstract = true)
public interface IOTour extends IOTourItem {

	public static final String OCLASS_NAME = "OTour";
	
	@XmlElement
	@DAOField(visualization = UIVisualizersRegistry.VISUALIZER_TABLE, inverse = "tour")
	public List<IOTourStep> getSteps();
}
