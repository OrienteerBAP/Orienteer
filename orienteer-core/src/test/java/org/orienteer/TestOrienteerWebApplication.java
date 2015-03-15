package org.orienteer;

import org.orienteer.OrienteerWebApplication;

import com.google.inject.Singleton;

@Singleton
public class TestOrienteerWebApplication extends OrienteerWebApplication
{

	@Override
	public void init() {
		super.init();
		registerModule(TestSchemaInstaller.class);
	}

}
