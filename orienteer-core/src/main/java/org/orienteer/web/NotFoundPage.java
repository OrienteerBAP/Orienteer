package org.orienteer.web;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

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
