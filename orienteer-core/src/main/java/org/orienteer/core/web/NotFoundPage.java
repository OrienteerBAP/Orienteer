package org.orienteer.core.web;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.MountPath;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

/**
 * Error page for 404 code (resource not found)
 */
@MountPath("/404")
public class NotFoundPage extends SearchPage {

	public NotFoundPage() {
		super();
	}

	public NotFoundPage(IModel<String> model) {
		super(model);
	}

	public NotFoundPage(PageParameters parameters) {
		super(parameters);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		error(getLocalizer().getString("errors.pagenotfound", null));
	}
	
	@Override
	public boolean isErrorPage() {
		return true;
	}
	
	@Override
	protected boolean isClientInfoRequired() {
		return false;
	}
	
	@Override
	protected void setHeaders(WebResponse response) {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	}

}
