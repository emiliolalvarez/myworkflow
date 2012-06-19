package com.myworkflow.workflow;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.myworkflow.TaskResult;
import com.myworkflow.task.Task;

public class Workflow implements Runnable{
	
	private Map<String,Task> tasks = new ConcurrentHashMap<String, Task>();
	private WorkflowDefinition workflowDefinition;
	private String name;
	
	public void addTask(String taskName, Task task){
		this.tasks.put(taskName, task);
	}
	
	public Task getTask(String taskName){
		return tasks.get(taskName);
	}
	
	public Workflow(){
		
	}
	
	public Workflow(WorkflowDefinition workflowDefinition, String name){
		this.workflowDefinition = workflowDefinition;
		this.name = name;
	}
	
	public void runTask(String taskName) throws Exception{
		System.out.println(taskName);
		Task t = this.getTask(taskName);
		TaskResult tr = t.runTask();
		System.out.println("Task: "+tr.getMessage());
		String next = this.workflowDefinition.getNextTransitionName(taskName,tr);
		if(next!=null){
			workflowDefinition.updateWorkflowStatus(next, this);
			runTask(next);
		}
		else{
			System.out.println("Workflow finished");
			workflowDefinition.finishWorkflow(this);
		}
	}
	
	public String getName(){
		return name;
	}
	
	public WorkflowDefinition getWorkflowDefinition(){
		return workflowDefinition;
	}
	
	public void run(){
		try{
			runTask(this.workflowDefinition.getFirstTransitionTaskName());
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Workflow object error!");
		}
	}
	
}
