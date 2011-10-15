package com.workflow.task;

import com.workflow.workflow.Workflow;


public class TaskOnError extends Task {

	public TaskOnError(Workflow w){
		super(w);
	}
	
	@Override
	public TaskResult runTask() {
			return null;
	}

}
