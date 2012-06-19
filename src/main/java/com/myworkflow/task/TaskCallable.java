package com.myworkflow.task;

import java.util.concurrent.Callable;

public abstract class TaskCallable implements Callable<TaskAsyncResult> {

	protected TaskAsync task;
	
	public TaskCallable(TaskAsync t){
		task = t;
	}
	
	public abstract TaskAsyncResult call();
}
