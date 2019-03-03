package org.orienteer.core.component;

import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.web.BasePage;
import org.orienteer.core.web.OrienteerBasePage;

import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

public class WicketPropertyResolverPage extends OrienteerBasePage<ODocument> {
	
	public WicketPropertyResolverPage() {
		super(new ODocumentModel(OrienteerWebSession.get().getEffectiveUser().getIdentity()));
	}
}
