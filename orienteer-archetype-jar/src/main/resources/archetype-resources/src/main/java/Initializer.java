package ${package};

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.orienteer.core.OrienteerWebApplication;

public class Initializer implements IInitializer
{
	@Override
	public void init(Application application) {
		OrienteerWebApplication app = (OrienteerWebApplication)application;
		app.registerModule(Module.class);
		app.mountPages("${package}.web");
	}

	@Override
	public void destroy(Application application) {
		OrienteerWebApplication app = (OrienteerWebApplication)application;
	}
	
}
