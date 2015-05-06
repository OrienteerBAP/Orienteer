package org.orienteer.core.util;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.service.IOClassIntrospector;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link IChoiceRenderer} for {@link ODocument}s. Display value is an document's name
 */
public class ODocumentChoiceRenderer implements IChoiceRenderer<ODocument>
{
	private transient IOClassIntrospector oClassIntrospector;
	
	protected IOClassIntrospector getOClassIntrospector()
	{
		if(oClassIntrospector==null)
		{
			oClassIntrospector = OrienteerWebApplication.get().getOClassIntrospector();
		}
		return oClassIntrospector;
	}
	
	@Override
	public Object getDisplayValue(ODocument object) {
		return getOClassIntrospector().getDocumentName(object);
	}

	@Override
	public String getIdValue(ODocument object, int index) {
		return Integer.toString(index);
	}

}
