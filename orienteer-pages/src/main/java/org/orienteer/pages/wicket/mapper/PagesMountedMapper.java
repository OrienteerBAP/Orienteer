package org.orienteer.pages.wicket.mapper;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.pages.module.PagesModule;
import org.orienteer.pages.web.EmbeddedWebPage;
import org.orienteer.pages.web.FullWebPage;

import java.util.Objects;

/**
 * Mounted mapper to link to pageId
 */
public class PagesMountedMapper extends AbstractMountedMapper {
	
	private final ORID pageIdentity;
	
	private static Class<? extends IRequestablePage> getPageClass(ODocument page) {
		Boolean embedded = page.field(PagesModule.OPROPERTY_EMBEDDED);
		return embedded == null || embedded ? EmbeddedWebPage.class : FullWebPage.class;
	}

	public PagesMountedMapper(ODocument page) {
		super(page.field(PagesModule.OPROPERTY_PATH), getPageClass(page), new OPageParametersEncoder(page.getIdentity()));
		pageIdentity = page.getIdentity();
	}

	/**
	 * @param pageId {@link OIdentifiable} page identifiable
	 * @return true if this mapper serving given page
	 */
	public boolean isServing(OIdentifiable pageId) {
		return pageIdentity.equals(pageId);
	}

	@Override
	protected boolean isMatch(PageParameters parameters) {
		String sv = parameters != null ? parameters.get(OPageParametersEncoder.PAGE_IDENTITY).toOptionalString() : null;
		return Objects.equals(sv, pageIdentity.toString());
	}

}
