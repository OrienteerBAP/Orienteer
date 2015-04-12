package ${package};

import org.orienteer.OrienteerWebApplication;
import org.orienteer.modules.PerspectivesModule;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

public class MyWebApplication extends OrienteerWebApplication
{
	@Override
	public void init()
	{
		super.init();
		new AnnotatedMountScanner().scanPackage("${package}.web").mount(this);
		registerModule(DataModel.class);
	}
	
}
