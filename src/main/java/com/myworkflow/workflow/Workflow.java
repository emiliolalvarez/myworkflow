package com.myworkflow.workflow;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.myworkflow.TaskResult;
import com.myworkflow.task.Task;

public class Workflow implements Runnable{
	
	private Map<String,Task> tasks = new ConcurrentHashMap<String, Task>();
	private TransitionDefinition transitionDefinition;
	private WorkflowApplicationContext context;
	private String name;
	
	public void addTask(String taskName, Task task){
		this.tasks.put(taskName, task);
	}
	
	public Task getTask(String taskName){
		return tasks.get(taskName);
	}
	
	public Workflow(){
		
	}
	
	public Workflow(WorkflowApplicationContext context, String name){
		this.context = context;
		this.transitionDefinition = context.getTransitionDefinition();
		this.name = name;
	}
	
	public WorkflowApplicationContext getContext(){
		return context;
	}
	
	public void runTask(String taskName) throws Exception{
		System.out.println(taskName);
		Task t = this.getTask(taskName);
		TaskResult tr = t.runTask();
		System.out.println("Task: "+tr.getMessage());
		String next = this.transitionDefinition.getNextTransitionName(taskName,tr);
		if(next!=null){
			runTask(next);
		}
		else{
			System.out.println("Workflow finished");
			context.finishWorkflow(this);
		}
	}
	
	public String getName(){
		return name;
	}
	
	public TransitionDefinition getTransitionDefinition(){
		return transitionDefinition;
	}
	
	public void run(){
		try{
			runTask(this.transitionDefinition.getFirstTransitionTaskName());
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Workflow object error!");
		}
	}
	
}
