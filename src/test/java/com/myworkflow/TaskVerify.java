package com.myworkflow;

import com.myworkflow.task.Task;
import com.myworkflow.workflow.Workflow;


public class TaskVerify extends Task {

	public TaskVerify(Workflow w){
		super(w);
	}
	
	@Override
	public TaskResult runTask() {
		return new TaskResult("success","Verify ok!");
		//return new TaskResult(TaskResult.STATUS_ERROR,"Verify failed!");
	}

}
