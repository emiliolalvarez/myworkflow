package com.workflow.task;

import com.workflow.workflow.Workflow;


public abstract class TaskAsync extends Task implements TaskAsyncObserver {

	public TaskAsync(Workflow w) {
		super(w);
	}

	public Task getCurrentTask(){
		return this;
	}
	
	@Override
	public abstract void notifyAsyncTaskFinalization();

	@Override
	public abstract TaskResult runTask();
	
	

}
