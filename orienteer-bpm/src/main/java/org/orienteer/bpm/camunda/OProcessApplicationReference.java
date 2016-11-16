package org.orienteer.bpm.camunda;

import org.apache.wicket.util.io.IClusterable;
import org.camunda.bpm.application.ProcessApplicationInterface;
import org.camunda.bpm.application.ProcessApplicationReference;
import org.camunda.bpm.application.ProcessApplicationUnavailableException;
import org.orienteer.core.OrienteerWebApplication;

/**
 * Defines {@link ProcessApplicationReference} to currently registered in the system {@link ProcessApplicationInterface} 
 */
public class OProcessApplicationReference implements ProcessApplicationReference, IClusterable {

	public static final OProcessApplicationReference INSTANCE = new OProcessApplicationReference();

	@Override
	public String getName() {
		return OProcessApplication.DEFAULT_PROCESS_APPLICATION_NAME;
	}

	@Override
	public ProcessApplicationInterface getProcessApplication() throws ProcessApplicationUnavailableException {
		OrienteerWebApplication app = OrienteerWebApplication.lookupApplication();
		return app.getMetaData(OProcessApplication.PROCESS_APPLICATION_KEY);
	}

}
