package com.myworkflow.task;

import com.myworkflow.TaskResult;
import com.myworkflow.workflow.Workflow;


public abstract class TaskAsync extends Task implements TaskAsyncObserver {

	public TaskAsync(Workflow w) {
		super(w);
	}

	public Task getCurrentTask(){
		return this;
	}
	
	public abstract void notifyAsyncTaskFinalization(TaskAsyncResult r);

	@Override
	public abstract TaskResult runTask();
	
}
