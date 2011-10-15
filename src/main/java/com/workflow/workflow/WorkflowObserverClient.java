package com.workflow.workflow;


public class WorkflowObserverClient implements  WorkflowObserver {

	private WorkflowDefinition wd;
	
	public WorkflowObserverClient(WorkflowDefinition wd){
		this.wd = wd;
	}
	
	@Override
	public void notifyFinishedEvent(Workflow w) {
		
		System.out.println("+++++++++++++++");
		System.out.println("Workflow "+w.getName()+" finished!");
		System.out.println("+++++++++++++++");

	}
}
