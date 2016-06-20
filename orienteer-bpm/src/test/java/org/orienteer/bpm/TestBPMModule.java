package org.orienteer.bpm;

import org.orienteer.bpm.camunda.OProcessApplicationReference;
import org.orienteer.bpm.camunda.OProcessEngineConfiguration;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.IOrienteerModule;

import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;

import static org.junit.Assert.*;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import junit.framework.AssertionFailedError;

@RunWith(OrienteerTestRunner.class)
@Singleton
public class TestBPMModule {
	@Inject
	private OrienteerTester tester;

	@Rule
	public ProcessEngineRule processEngineRule = new ProcessEngineRule(
			BpmPlatform.getProcessEngineService().getDefaultProcessEngine());

	@Test
	public void testModuleLoaded() {
		OrienteerWebApplication app = tester.getApplication();
		assertNotNull(app);
		IOrienteerModule module = app.getModuleByName("bpm");
		assertNotNull(module);
		assertTrue(module instanceof BPMModule);
	}

	@Test
	@Deployment(resources = { "example.bpmn" })
	public void testEndProcessByCancelMessage() {
		RuntimeService runtimeService = processEngineRule.getRuntimeService();
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Process_1");
		assertProcessNotEnded(processInstance.getId());

		runtimeService.createMessageCorrelation("Message-Cancel").processInstanceId(processInstance.getId())
				.correlate();

	}

	@Deployment(resources = { "example-simple.bpmn" })
	public void testEndProcessByCancelMessageSimple() {
		RuntimeService runtimeService = processEngineRule.getRuntimeService();
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Process_1");
		assertProcessNotEnded(processInstance.getId());

		runtimeService.createMessageCorrelation("Message-Approve").processInstanceId(processInstance.getId())
				.correlate();

		assertProcessEnded(processInstance.getId());
	}

	@Deployment(resources = { "example-sequence.bpmn" })
	public void testEndProcessByCancelMessageSequence() {
		RuntimeService runtimeService = processEngineRule.getRuntimeService();
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Process_1");
		assertProcessNotEnded(processInstance.getId());

		runtimeService.createMessageCorrelation("Message-Request").processInstanceId(processInstance.getId())
				.correlate();
		assertProcessNotEnded(processInstance.getId());

		runtimeService.createMessageCorrelation("Message-Approve").processInstanceId(processInstance.getId())
				.correlate();
		assertProcessEnded(processInstance.getId());
	}

	public void assertProcessNotEnded(String processInstanceId) {
		ProcessInstance processInstance = processEngineRule.getProcessEngine().getRuntimeService()
				.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

		if (processInstance == null) {
			throw new AssertionFailedError(
					"Expected not finished process instance '" + processInstanceId + "' but it was not in the db");
		}
	}

	public void assertProcessEnded(String processInstanceId) {
		ProcessInstance processInstance = processEngineRule.getProcessEngine().getRuntimeService()
				.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

		if (processInstance != null) {
			throw new AssertionFailedError(
					"expected finished process instance '" + processInstanceId + "' but it was still in the db");
		}
	}
}
