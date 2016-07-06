package org.orienteer.bpm;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsynchTestDelegate implements JavaDelegate {
	  protected static final Logger log = LoggerFactory.getLogger(AsynchTestDelegate.class);
	  private static int executed = 0;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		executed+=1;
	    log.info("TestDelegate called for execution: "+execution.getCurrentActivityName()+" executed: "+executed);
	    //Delay is required due to the following issue: https://app.camunda.com/jira/browse/CAM-6370
	    //TODO: remove when issue will be resolved
	    Thread.sleep(20);
	}
	
	public static void resetExecuted() {
		executed = 0;
	}
	
	public static int getExecuted() {
		return executed;
	}
}