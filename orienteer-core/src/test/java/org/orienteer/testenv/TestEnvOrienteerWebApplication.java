package org.orienteer.testenv;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.SchemaInstallerTest;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class TestEnvOrienteerWebApplication extends OrienteerWebApplication
{
	private static final Logger LOG = LoggerFactory.getLogger(TestEnvOrienteerWebApplication.class);

	@Override
	public void init() {
		super.init();
		registerModule(SchemaInstallerTest.class);
	}

	@Override
	protected void onDestroy() {
		LOG.info("On destroy");
		super.onDestroy();
	}
}
