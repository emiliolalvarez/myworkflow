package com.myworkflow.task;

import com.myworkflow.TaskResult;
import com.myworkflow.workflow.Workflow;

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
