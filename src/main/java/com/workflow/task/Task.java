package com.workflow.task;

import com.workflow.workflow.Workflow;

public abstract class Task {
	
	
	protected String name;
	
	protected Workflow workflow;
	
	public abstract TaskResult runTask();
	
	public Task(Workflow w){
		this.workflow = w;
	}
	
	public Workflow getWorkflow(){
		return this.workflow;
	}
	
	
		
}
