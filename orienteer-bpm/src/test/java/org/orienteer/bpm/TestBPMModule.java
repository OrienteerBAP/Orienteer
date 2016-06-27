package org.orienteer.bpm;

import org.orienteer.bpm.camunda.OProcessApplicationReference;
import org.orienteer.bpm.camunda.OProcessEngineConfiguration;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.IOrienteerModule;

import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Message;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jna.platform.win32.WinNT.LOGICAL_PROCESSOR_RELATIONSHIP;

import junit.framework.AssertionFailedError;

@RunWith(OrienteerTestRunner.class)
@Singleton
public class TestBPMModule {

	private static final Logger LOG = LoggerFactory.getLogger(TestBPMModule.class);

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
	public void testDeployProcess() {

		BpmnModelInstance model = Bpmn.createExecutableProcess("testProcess").done();

		Message message1 = model.newInstance(Message.class);
		message1.setName("orderCancelled");
		model.getDefinitions().addChildElement(message1);

		Message message2 = model.newInstance(Message.class);
		message2.setName("orderCompleted");
		model.getDefinitions().addChildElement(message2);

		BpmnModelInstance theProcess = model.<Process> getModelElementById("testProcess").builder().startEvent()
				.parallelGateway().receiveTask().message(message1).endEvent().moveToLastGateway().receiveTask()
				.message(message2).endEvent().done();

		ProcessEngine processEngine = processEngineRule.getProcessEngine();

		org.camunda.bpm.engine.repository.Deployment deployment = processEngine.getRepositoryService()
				.createDeployment().addModelInstance("test.bpmn", theProcess).deploy();

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("testVar", "testVarValue");
		ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey("testProcess",
				vars);

		String varValue = (String) processEngine.getRuntimeService().getVariable(processInstance.getId(), "testVar");
		assertEquals("testVarValue", varValue);

		processEngine.getRuntimeService().setVariable(processInstance.getId(), "testVar2", new Boolean(true));

		Boolean varValue2 = (Boolean) processEngine.getRuntimeService().getVariable(processInstance.getId(),
				"testVar2");
		assertEquals(new Boolean(true), varValue2);

		List<Execution> executions = processEngine.getRuntimeService().createExecutionQuery()
				.processInstanceId(processInstance.getId()).list();

		String executionId = null;
		for (Execution execution : executions) {
			if (!execution.getId().equals(processInstance.getId())) {
				executionId = execution.getId();
				LOG.info("Creating local variable for execution id: " + executionId);
				processEngine.getRuntimeService().setVariableLocal(executionId, "testVarLocal", "testVarLocalValue");
				break;
			}
		}
		assertNotNull(executionId);
		String varValueLocal = (String) processEngine.getRuntimeService().getVariableLocal(executionId, "testVarLocal");
		assertEquals("testVarLocalValue", varValueLocal);

		processEngine.getRuntimeService().createMessageCorrelation("orderCancelled")
				.processInstanceId(processInstance.getId()).correlate();

		processEngine.getRuntimeService().createMessageCorrelation("orderCompleted")
				.processInstanceId(processInstance.getId()).correlate();

		assertProcessEnded(processInstance.getId());

		processEngine.getRepositoryService().deleteDeployment(deployment.getId(), true);

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

	@Test
	@Deployment(resources = { "example-simple.bpmn" })
	public void testEndProcessByCancelMessageSimple() {
		RuntimeService runtimeService = processEngineRule.getRuntimeService();
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Process_1");
		assertProcessNotEnded(processInstance.getId());

		runtimeService.createMessageCorrelation("Message-Approve").processInstanceId(processInstance.getId())
				.correlate();

		assertProcessEnded(processInstance.getId());
	}

	@Test
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

	@Test
	@Deployment(resources = { "example-sequence.bpmn" })
	public void testCorrelateByMessageName() {
		RuntimeService runtimeService = processEngineRule.getRuntimeService();
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Process_1");
		assertProcessNotEnded(processInstance.getId());

		// correlate by message name
		runtimeService.createMessageCorrelation("Message-Request").correlate();
		assertProcessNotEnded(processInstance.getId());

		// correlate by message name and process id
		runtimeService.createMessageCorrelation("Message-Approve").processInstanceId(processInstance.getId())
				.correlate();
		assertProcessEnded(processInstance.getId());
	}

	@Test
	@Deployment(resources = { "example-sequence.bpmn" })
	public void testCorrelateByBusinessKey() {
		RuntimeService runtimeService = processEngineRule.getRuntimeService();
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Process_1", "business key");
		assertProcessNotEnded(processInstance.getId());

		runtimeService.createMessageCorrelation("Message-Request")
				.processInstanceBusinessKey(processInstance.getBusinessKey()).correlate();
		assertProcessNotEnded(processInstance.getId());

		runtimeService.createMessageCorrelation(null).processInstanceBusinessKey(processInstance.getBusinessKey())
				.correlateAll();
		assertProcessEnded(processInstance.getId());
	}

	@Test
	@Deployment(resources = { "example-sequence.bpmn" })
	public void testCorrelateByVariables() {
		RuntimeService runtimeService = processEngineRule.getRuntimeService();
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("testString", "testVarString");
		vars.put("testBoolean", new Boolean(true));
		vars.put("testLong", new Long(15));

		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Process_1", vars);
		assertProcessNotEnded(processInstance.getId());

		runtimeService.createMessageCorrelation("Message-Request")
				.processInstanceVariableEquals("testString", "testVarString").correlate();
		assertProcessNotEnded(processInstance.getId());

		runtimeService.createMessageCorrelation(null).processInstanceVariableEquals("testString", "NonExisting")
				.correlateAll();
		assertProcessNotEnded(processInstance.getId());
		
		runtimeService.createMessageCorrelation(null).processInstanceVariableEquals("testString", "testVarString")
		.correlate();
		
		assertProcessEnded(processInstance.getId());
	}

	@Test
	@Deployment(resources = { "loop.bpmn" })
	public void testLoop() {
		RuntimeService runtimeService = processEngineRule.getRuntimeService();
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Process_1");

		for (int i = 0; i < 2; i++) {
			runtimeService.createMessageCorrelation("continue").processInstanceId(processInstance.getId())
					.correlateAll();
			if (i % 10 == 0) {
				LOG.info("Iteration: " + i);
			}
		}

		runtimeService.createMessageCorrelation("stop").processInstanceId(processInstance.getId()).correlateAll();

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
