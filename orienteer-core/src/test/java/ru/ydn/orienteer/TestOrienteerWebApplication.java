package ru.ydn.orienteer;

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
