package com.myworkflow.task;

public interface TaskAsyncObserver {
	
	public void notifyAsyncTaskFinalization(TaskAsyncResult r);
	
}
