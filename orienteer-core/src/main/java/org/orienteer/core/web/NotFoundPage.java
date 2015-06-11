package org.orienteer.core.web;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.MountPath;

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

}
