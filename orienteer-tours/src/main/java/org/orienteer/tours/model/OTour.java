package org.orienteer.tours.model;

import static org.orienteer.core.util.CommonUtils.mapIdentifiables;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * DocumentWrapper for tours
 */
public class OTour extends AbstractOTourItem {

	public static final String OCLASS_NAME = "OTour";
	
	public static final String OPROPERTY_STEPS = "steps";
	
	public OTour() {
		super(OCLASS_NAME);
	}

	public OTour(ODocument iDocument) {
		super(iDocument);
	}

	public OTour(ORID iRID) {
		super(iRID);
	}
	
	@XmlElement
	public List<OTourStep> getSteps() {
		return mapIdentifiables(getDocument().field(OPROPERTY_STEPS), OTourStep::new);
	}
}
