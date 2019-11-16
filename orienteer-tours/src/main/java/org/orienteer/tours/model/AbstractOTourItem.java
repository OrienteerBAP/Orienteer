package org.orienteer.tours.model;

import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.util.LocalizeFunction;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * Abstract OTourItem to cover tours and steps 
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractOTourItem extends ODocumentWrapper {
	public static final String OCLASS_NAME = "OTourItem";
	
	public static final String OPROPERTY_TITLE = "title";
	public static final String OPROPERTY_ALIAS = "alias";
	public static final String OPROPERTY_PATH = "path";

	public AbstractOTourItem(ODocument iDocument) {
		super(iDocument);
	}

	public AbstractOTourItem(ORID iRID) {
		super(iRID);
	}
	
	protected AbstractOTourItem(String iClassName) {
		super(iClassName);
	}

	@XmlElement
	public String getTitle() {
		return localize(getDocument().field(OPROPERTY_TITLE));
	}
	
	@XmlElement
	public String getAlias() {
		return getDocument().field(OPROPERTY_ALIAS);
	}
	
	@XmlElement
	public String getPath() {
		return getDocument().field(OPROPERTY_PATH);
	}
	
	protected static String localize(Object object) {
		return LocalizeFunction.getInstance().apply(object);
	}
	
	public boolean isAllowed() {
		return isAllowed(RequestCycle.get().getRequest().getUrl());
	}
	
	public boolean isAllowed(Url url) {
		String path = getPath();
		return Strings.isEmpty(path)?true:url.toString().matches(path);
	}
}
