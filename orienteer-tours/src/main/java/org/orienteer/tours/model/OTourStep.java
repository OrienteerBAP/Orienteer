package org.orienteer.tours.model;

import javax.xml.bind.annotation.XmlElement;

import org.orienteer.core.util.LocalizeFunction;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * DocumentWrapper for steps 
 */
public class OTourStep extends AbstractOTourItem {
	
	public static final String OCLASS_NAME = "OTourStep";
	
	public static final String OPROPERTY_TOUR = "tour";
	public static final String OPROPERTY_CONTENT = "content";
	public static final String OPROPERTY_ELEMENT = "element";

	public OTourStep() {
		super(OCLASS_NAME);
	}

	public OTourStep(ODocument iDocument) {
		super(iDocument);
	}

	public OTourStep(ORID iRID) {
		super(iRID);
	}
	
	public OTour getTour() {
		return new OTour((ODocument) getDocument().field(OPROPERTY_TOUR));
	}
	
	@XmlElement
	public String getContent() {
		return LocalizeFunction.getInstance().apply(getDocument().field(OPROPERTY_CONTENT));
	}
	
	@XmlElement
	public String getElement() {
		return getDocument().field(OPROPERTY_ELEMENT);
	}
	
}
