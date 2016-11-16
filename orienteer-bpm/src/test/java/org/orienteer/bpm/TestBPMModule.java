package org.orienteer.bpm;

import org.orienteer.bpm.camunda.OProcessApplicationReference;
import org.orienteer.bpm.camunda.OProcessEngineConfiguration;
import org.orienteer.bpm.camunda.handler.ExecutionEntityHandler;
import org.orienteer.bpm.camunda.handler.HandlersManager;
import org.orienteer.bpm.camunda.handler.TaskEntityHandler;
import org.orienteer.bpm.camunda.handler.UserEntityHandler;
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
import java.util.Timer;
import java.util.TimerTask;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.externaltask.LockedExternalTask;
import org.camunda.bpm.engine.impl.cfg.IdGenerator;
import org.camunda.bpm.engine.impl.jobexecutor.JobExecutor;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
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
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
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

	@Test
	@Deployment(resources = { "asynch-test.bpmn" })
	public void testParallelExecution() throws InterruptedException {
		AsynchTestDelegate.resetExecuted();
		ProcessInstance processInstance = processEngineRule.getRuntimeService().startProcessInstanceByKey("asynchtest");
		// describeProcess(processInstance);
		waitForJobExecutorToProcessAllJobs(10000);
//		assertEquals(2, AsynchTestDelegate.getExecuted());
		assertTrue(AsynchTestDelegate.getExecuted()>=2);
		// waitForProcessCompletion(processInstance.getProcessInstanceId(),
		// 20000);
		assertProcessEnded(processInstance.getId(), true);

	}

	/**
	 * Test external task entities
	 */
	@Test
	@Deployment(resources = {"external-task.bpmn"})
	public void testExternalTask() {
		ProcessInstance processInstance = processEngineRule.getRuntimeService().startProcessInstanceByKey("externaltask");
		assertProcessNotEnded(processInstance.getId());
		List<ExternalTask> tasks = processEngineRule.getExternalTaskService().createExternalTaskQuery()
										.topicName("ExternalTopic")
										.processInstanceId(processInstance.getId()).list();
		assertEquals(1, tasks.size());
		
		
		List<LockedExternalTask> lockedTasks = processEngineRule.getExternalTaskService().fetchAndLock(100, "JUnit")
				.topic("ExternalTopic", 5000).execute();
		assertFalse(lockedTasks.isEmpty());
		for(LockedExternalTask task : lockedTasks) {
			processEngineRule.getExternalTaskService().complete(task.getId(), "JUnit");
		}
		tasks = processEngineRule.getExternalTaskService().createExternalTaskQuery()
				.topicName("ExternalTopic")
				.processInstanceId(processInstance.getId()).list();
		assertTrue(tasks.isEmpty());
		assertProcessEnded(processInstance.getId());
	}
	
	@Test
	@Deployment(resources = {"user-task.bpmn"})
	public void testUserTask() {
		ProcessInstance processInstance = processEngineRule.getRuntimeService().startProcessInstanceByKey("user-task");
		assertProcessNotEnded(processInstance.getId());
		ODatabaseDocument db = tester.getDatabase();
		for(ODocument doc : db.browseClass(TaskEntityHandler.OCLASS_NAME)){
			System.out.println("Task: "+doc);
		}
		List<Task> tasks = processEngineRule.getTaskService().createTaskQuery().taskAssignee("admin").processInstanceId(processInstance.getId()).list();
		assertNotNull(tasks);
		assertFalse(tasks.isEmpty());
		assertEquals(1, tasks.size());
		tasks = processEngineRule.getTaskService().createTaskQuery().taskCandidateGroup("writer").processInstanceId(processInstance.getId()).list();
		assertNotNull(tasks);
		assertFalse(tasks.isEmpty());
		assertEquals(1, tasks.size());
		Task task = tasks.get(0);
		processEngineRule.getTaskService().complete(task.getId());
		assertProcessEnded(processInstance.getId());
	}
	
	private static boolean touchedFromScript = false; 
	public static void touchFromScript() {
		touchedFromScript = true;
	}
	
	@Test
	@Deployment(resources = {"execute-script.bpmn"})
	public void testExecuteScriptSimple() {
		touchedFromScript=false;
		Map<String, Object> variables = new HashMap<>();
		variables.put("script", "org.orienteer.bpm.TestBPMModule.touchFromScript();");
		ProcessInstance processInstance = processEngineRule.getRuntimeService().startProcessInstanceByKey("execute-script", variables);
		assertProcessEnded(processInstance.getId());
		assertTrue(touchedFromScript);
	}
	
	@Test
	@Deployment(resources = {"execute-script.bpmn"})
	public void testExecuteOrientDBScript() {
		touchedFromScript=false;
		Map<String, Object> variables = new HashMap<>();
		variables.put("script", "org.orienteer.bpm.TestBPMModule.touchFromScript();");
		ProcessInstance processInstance = processEngineRule.getRuntimeService().startProcessInstanceByKey("execute-script", variables);
		assertProcessEnded(processInstance.getId());
		assertTrue(touchedFromScript);
	}
	
	

	private static class InterruptTask extends TimerTask {
		protected boolean timeLimitExceeded = false;
		protected Thread thread;

		public InterruptTask(Thread thread) {
			this.thread = thread;
		}

		public boolean isTimeLimitExceeded() {
			return timeLimitExceeded;
		}

		@Override
		public void run() {
			timeLimitExceeded = true;
			thread.interrupt();
		}
	}

	public boolean areJobsAvailable() {
		List<Job> list = processEngineRule.getManagementService().createJobQuery().list();
		for (Job job : list) {
			if (!job.isSuspended() && job.getRetries() > 0
					&& (job.getDuedate() == null || ClockUtil.getCurrentTime().after(job.getDuedate()))) {
				return true;
			}
		}
		return false;
	}

	public void waitForJobExecutorToProcessAllJobs(long maxMillisToWait) {
		long intervalMillis = 1000;
		Timer timer = new Timer();
		InterruptTask task = new InterruptTask(Thread.currentThread());
		timer.schedule(task, maxMillisToWait);
		boolean areJobsAvailable = true;
		try {
			while (areJobsAvailable && !task.isTimeLimitExceeded()) {
				Thread.sleep(intervalMillis);
				try {
					areJobsAvailable = areJobsAvailable();
				} catch (Throwable t) {
					// Ignore, possible that exception occurs due to
					// locking/updating of table on MSSQL when
					// isolation level doesn't allow READ of the table
				}
			}
		} catch (InterruptedException e) {
		} finally {
			timer.cancel();
		}
		if (areJobsAvailable) {
			throw new ProcessEngineException("time limit of " + maxMillisToWait + " was exceeded");
		}
	}

	public void waitForProcessCompletion(String processIsntanceId, long maxMillisToWait) {
		long intervalMillis = 500;

		Timer timer = new Timer();
		InterruptTask task = new InterruptTask(Thread.currentThread());
		timer.schedule(task, maxMillisToWait);
		boolean isProcessFinished = isProcessFinished(processIsntanceId);
		try {
			while (!isProcessFinished && !task.isTimeLimitExceeded()) {
				Thread.sleep(intervalMillis);
				try {
					isProcessFinished = isProcessFinished(processIsntanceId);
				} catch (Throwable t) {
					// Ignore, possible that exception occurs due to
					// locking/updating of table on MSSQL when
					// isolation level doesn't allow READ of the table
				}
			}
		} catch (InterruptedException e) {
		} finally {
			timer.cancel();
		}
		if (!isProcessFinished) {
			throw new ProcessEngineException(
					"Process not finished: time limit of " + maxMillisToWait + " was exceeded");
		}

	}

	public boolean isProcessFinished(String processInstanceId) {
		ProcessInstance processInstance = processEngineRule.getProcessEngine().getRuntimeService()
				.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		return processInstance == null || processInstance.isEnded();
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
		assertProcessEnded(processInstanceId, false);
	}

	public void assertProcessEnded(String processInstanceId, boolean soft) {
		ProcessInstance processInstance = processEngineRule.getProcessEngine().getRuntimeService()
				.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

		if (processInstance != null && (!soft || !processInstance.isEnded())) {

			throw new AssertionFailedError(
					"expected finished process instance '" + processInstanceId + "' but it was still in the db");
		}
	}
}
