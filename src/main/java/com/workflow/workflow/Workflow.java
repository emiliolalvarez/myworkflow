package com.workflow.workflow;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.workflow.task.Task;
import com.workflow.task.TaskResult;

public class Workflow implements Runnable{
	
	private Map<String,Task> tasks = new ConcurrentHashMap<String, Task>();
	private WorkflowDefinition workflowDefinition;
	private String name;
	
//	private WorkflowContext context;
	
	
	public void addTask(String taskName, Task task){
		this.tasks.put(taskName, task);
	}
	
	public Task getTask(String taskName){
		return this.tasks.get(taskName);
	}
	
	public Workflow(){
		
	}
	
	public Workflow(WorkflowDefinition workflowDefinition, String name){
		this.workflowDefinition = workflowDefinition;
		this.name = name;
	}
	
	public void runTask(String taskName){
		Task t = this.getTask(taskName);
		TaskResult tr = t.runTask();
		System.out.println("Task: "+tr.getMessage());
		String next = this.workflowDefinition.getNextTransitionName(taskName,tr);
		if(next!=null){
			workflowDefinition.updateWorkflowStatus(next, this);
			runTask(next);
		}
		else{
			this.workflowDefinition.finishWorkflow(this);
		}
	}
	
	public String getName(){
		return this.name;
	}
	
	public WorkflowDefinition getWorkflowDefinition(){
		return this.workflowDefinition;
	}
	
	public void run(){
		
		this.runTask(this.workflowDefinition.getFirstTransitionTaskName());
		
	}
	
	
}
