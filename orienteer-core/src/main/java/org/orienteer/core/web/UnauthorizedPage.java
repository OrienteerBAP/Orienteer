package org.orienteer.core.web;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.orienteer.core.MountPath;

/**
 * Error page for 403 code (access forbidden)
 */
@MountPath("/403")
public class UnauthorizedPage extends OrienteerBasePage<Void>{

	public UnauthorizedPage() {
		super();
	}

	public UnauthorizedPage(IModel<Void> model) {
		super(model);
	}

	public UnauthorizedPage(PageParameters parameters) {
		super(parameters);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		error(getLocalizer().getString("errors.unauthorized", null));
	}
	
	@Override
	public IModel<String> getTitleModel() {
		return new ResourceModel("errors.unauthorized.title");
	}
	
}
