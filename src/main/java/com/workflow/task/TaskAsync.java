package com.workflow.task;

import com.workflow.workflow.Workflow;


public class TaskAsync extends Task implements TaskAsyncObserver {

	public TaskAsync(Workflow w) {
		super(w);
	}

	@Override
	public void notifyAsyncTaskFinalization() {
	}

	@Override
	public TaskResult runTask() {
		return null;
	}

}
