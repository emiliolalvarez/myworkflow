package com.myworkflow.task;

public class TaskAsyncResult {
	
	private TaskAsync taskAsync;
	private TaskCallable taskCallable;
	private String result;
	
	public TaskAsyncResult(String result,TaskAsync taskAsync,TaskCallable taskCallable){
		this.taskAsync = taskAsync;
		this.taskCallable = taskCallable;
	}
	
	public TaskAsync getTaskAsync(){
		return taskAsync;
	}
	
	public TaskCallable getTaskCallable(){
		return taskCallable;
	}
	
	public String getResult(){
		return result;
	}

}
