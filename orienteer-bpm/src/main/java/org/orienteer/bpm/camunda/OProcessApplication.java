package org.orienteer.bpm.camunda;

import org.apache.wicket.MetaDataKey;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.application.AbstractProcessApplication;
import org.camunda.bpm.application.ProcessApplicationInterface;
import org.camunda.bpm.application.ProcessApplicationReference;
import org.camunda.bpm.application.ProcessApplicationUnavailableException;
import org.orienteer.core.OrienteerWebApplication;

/**
 * OrientDB enables {@link AbstractProcessApplication} 
 */
public class OProcessApplication extends AbstractProcessApplication {

	public static final String DEFAULT_PROCESS_APPLICATION_NAME = "Orienteer";
	public static final MetaDataKey<ProcessApplicationInterface> PROCESS_APPLICATION_KEY = new MetaDataKey<ProcessApplicationInterface>() {};
	
	@Override
	public ProcessApplicationReference getReference() {
		return OProcessApplicationReference.INSTANCE;
	}

	@Override
	protected String autodetectProcessApplicationName() {
		return DEFAULT_PROCESS_APPLICATION_NAME;
	}

	@Override
	public void deploy() {
		super.deploy();
		OrienteerWebApplication app = OrienteerWebApplication.lookupApplication();
		app.setMetaData(PROCESS_APPLICATION_KEY, this);
	}

	@Override
	public void undeploy() {
		super.undeploy();
		OrienteerWebApplication app = OrienteerWebApplication.lookupApplication();
		app.setMetaData(PROCESS_APPLICATION_KEY, null);
	}
	
	protected OrienteerWebApplication getOrienteerWebApplication() {
		return OrienteerWebApplication.lookupApplication();
	}
	
	public static OProcessApplication get() {
		try {
			return (OProcessApplication) OProcessApplicationReference.INSTANCE.getProcessApplication();
		} catch (ProcessApplicationUnavailableException e) {
			return null;
		}
	}
	
}
