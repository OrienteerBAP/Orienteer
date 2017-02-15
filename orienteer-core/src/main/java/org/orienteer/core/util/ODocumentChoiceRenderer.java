package org.orienteer.core.util;

import java.util.List;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.service.IOClassIntrospector;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORecordId;
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
		return object!=null?object.getIdentity().toString():null;
	}

	@Override
	public ODocument getObject(String id,
			IModel<? extends List<? extends ODocument>> choicesModel) {
		if(ORecordId.isA(id)) {
			ORecordId rid = new ORecordId(id);
			ODocument ret =  rid.getRecord();
			List<? extends ODocument> choices = choicesModel.getObject();
			return choices!=null && choices.contains(ret)?ret:null;
		}
		return null;
	}

}
