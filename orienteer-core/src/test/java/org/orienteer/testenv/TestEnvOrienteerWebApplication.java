package org.orienteer.testenv;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.SchemaInstallerTest;

import com.google.inject.Singleton;

@Singleton
public class TestEnvOrienteerWebApplication extends OrienteerWebApplication
{

	@Override
	public void init() {
		super.init();
		registerModule(SchemaInstallerTest.class);
	}

}
