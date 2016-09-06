package org.orienteer.pages;

import org.apache.wicket.core.request.handler.IPageClassRequestHandler;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.orienteer.pages.module.PagesModule;
import org.orienteer.pages.web.EmbeddedWebPage;
import org.orienteer.pages.web.FullWebPage;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Mounted mapper to link to pageId
 */
public class PagesMountedMapper extends MountedMapper {
	
	private final ORID pageIdentity;
	
	private static Class<? extends IRequestablePage> getPageClass(OIdentifiable pageId) {
		ODocument doc = pageId.getRecord();
		Boolean embedded = doc.field(PagesModule.OPROPERTY_EMBEDDED);
		return embedded==null || embedded ?EmbeddedWebPage.class:FullWebPage.class;
	}

	public PagesMountedMapper(OIdentifiable pageId) {
		super((String)((ODocument)pageId.getRecord()).field(PagesModule.OPROPERTY_PATH), getPageClass(pageId), new OPageParametersEncoder(pageId.getIdentity()));
		pageIdentity = pageId.getIdentity();
	}
	
	public boolean isServing(OIdentifiable pageId) {
		return pageIdentity.equals(pageId);
	}
	
	@Override
	// We should ensure that request is for this pageId;
	public Url mapHandler(IRequestHandler requestHandler) {
		if(requestHandler instanceof IPageClassRequestHandler) {
			IPageClassRequestHandler handler = (IPageClassRequestHandler) requestHandler;
			if(OPageParametersEncoder.matchHandler(pageIdentity, handler)) return super.mapHandler(requestHandler);
			else return null;
		} else {
			return super.mapHandler(requestHandler);
		}
	}
	
}
