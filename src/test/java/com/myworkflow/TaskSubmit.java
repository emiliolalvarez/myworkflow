package com.myworkflow;

import com.myworkflow.task.Task;
import com.myworkflow.workflow.Workflow;

public class TaskSubmit extends Task {

	public TaskSubmit(Workflow w){
		super(w);
	}

	@Override
	public TaskResult runTask() {
		try {
			Thread.sleep(Math.round(Math.random()*8000));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new TaskResult("success", "submitted!");
	}

}
