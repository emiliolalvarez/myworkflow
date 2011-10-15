package com.workflow.task;

import java.util.concurrent.Callable;

public abstract class TaskCallable implements Callable<TaskAsync> {

	protected TaskAsync task;
	
	public TaskCallable(TaskAsync t){
		task = t;
	}
	
	public abstract TaskAsync call();
}
