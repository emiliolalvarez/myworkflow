package com.myworkflow;

import com.myworkflow.task.Task;
import com.myworkflow.workflow.Workflow;


public class TaskOnError extends Task {

	public TaskOnError(Workflow w){
		super(w);
	}
	
	@Override
	public TaskResult runTask() {
			return null;
	}

}
