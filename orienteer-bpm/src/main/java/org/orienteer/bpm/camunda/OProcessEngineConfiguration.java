package org.orienteer.bpm.camunda;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.jobexecutor.FoxFailedJobCommandFactory;
import org.camunda.bpm.engine.impl.persistence.GenericManagerFactory;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.orienteer.bpm.camunda.scripting.OResolverFactory;

/**
 * {@link ProcessEngineConfiguration} for OrientDB implementation
 *
 */
public class OProcessEngineConfiguration extends StandaloneProcessEngineConfiguration {
	
	@Override
	protected void initPersistenceProviders() {
		addSessionFactory(new OPersistenceSessionFactory());
		setIdentityProviderSessionFactory(identityProviderSessionFactory);
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
	
	@Override
	protected void initJpa() {
	}
	
	@Override
	protected void initJobExecutor() {
		super.initJobExecutor();
		jobExecutor.setAutoActivate(true);
	}
	
	@Override
	protected void initFailedJobCommandFactory() {
		if (failedJobCommandFactory == null) {
	      failedJobCommandFactory = new FoxFailedJobCommandFactory();
	    }
	}
	
	@Override
	protected void initIdentityProviderSessionFactory() {
		identityProviderSessionFactory = new GenericManagerFactory(OIdentityServiceProvider.class);
	}
	
	@Override
	protected void initScripting() {
		super.initScripting();
		resolverFactories.add(new OResolverFactory());
	}
	
	public static OProcessEngineConfiguration get() {
		return (OProcessEngineConfiguration) BpmPlatform.getDefaultProcessEngine().getProcessEngineConfiguration();
	}
}
