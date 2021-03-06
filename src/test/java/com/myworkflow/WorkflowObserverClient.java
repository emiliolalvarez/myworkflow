package com.myworkflow;

import com.myworkflow.workflow.Workflow;
import com.myworkflow.workflow.TransitionDefinition;
import com.myworkflow.workflow.WorkflowObserver;


public class WorkflowObserverClient implements  WorkflowObserver {

	private TransitionDefinition wd;
	
	public WorkflowObserverClient(TransitionDefinition wd){
		this.wd = wd;
	}
	
	public void notifyFinishedEvent(Workflow w) {
		System.out.println("First transition name: "+wd.getFirstTransitionTaskName());
		System.out.println("+++++++++++++++");
		System.out.println("Workflow "+w.getName()+" finished!");
		System.out.println("+++++++++++++++");
	}
}
