package org.orienteer.bpm.camunda;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration;

public class OProcessEngineConfiguration extends StandaloneProcessEngineConfiguration {
	
	public OProcessEngineConfiguration() {
		setExecutionTreePrefetchEnabled(false);
	}

	@Override
	protected void initPersistenceProviders() {
		addSessionFactory(new OPersistenceSessionFactory());
	}

	@Override
	public ProcessEngine buildProcessEngine() {
		super.buildProcessEngine();
		OPersistenceSession.staticInit(this);
		return processEngine;
	}

	@Override
	protected void initSqlSessionFactory() {
	}

	@Override
	protected void initDataSource() {
	}
}
