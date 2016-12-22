package org.orienteer.core.tasks;

public interface IRealTask {
	void setOTask(OTask otask);
	void start(OTaskData data);
	void stop();
}
