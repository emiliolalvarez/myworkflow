package com.myworkflow;

import com.myworkflow.workflow.Workflow;
import com.myworkflow.workflow.WorkflowDefinition;
import com.myworkflow.workflow.WorkflowObserver;


public class WorkflowObserverClient implements  WorkflowObserver {

	private WorkflowDefinition wd;
	
	public WorkflowObserverClient(WorkflowDefinition wd){
		this.wd = wd;
	}
	
	public void notifyFinishedEvent(Workflow w) {
		System.out.println("First transition name: "+wd.getFirstTransitionTaskName());
		System.out.println("+++++++++++++++");
		System.out.println("Workflow "+w.getName()+" finished!");
		System.out.println("+++++++++++++++");
	}
}
