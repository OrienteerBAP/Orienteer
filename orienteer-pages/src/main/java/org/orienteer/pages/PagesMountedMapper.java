package org.orienteer.pages;

import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.component.IRequestablePage;
import org.orienteer.pages.module.PagesModule;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Mounted mapper to link to pageId
 */
public class PagesMountedMapper extends MountedMapper {
	
	private final ORID pageIdentity;

	public PagesMountedMapper(OIdentifiable pagesId) {
		super((String)((ODocument)pagesId.getRecord()).field(PagesModule.OPROPERTY_PATH), PagesWebPage.class, new OPageParametersEncoder(pagesId.getIdentity()));
		pageIdentity = pagesId.getIdentity();
	}
	
	public boolean isServing(OIdentifiable pageId) {
		return pageIdentity.equals(pageId);
	}
	
}
