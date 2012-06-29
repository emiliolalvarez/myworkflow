package com.myworkflow.workflow;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.myworkflow.TaskResult;
import com.myworkflow.task.Task;

public class Workflow implements Runnable{
	
	private Map<String,Task> tasks = new ConcurrentHashMap<String, Task>();
	private TransitionDefinition transitionDefinition;
	private WorkflowApplicationContext context;
	private String name;
	private final Logger LOGGER = Logger.getLogger(Workflow.class);
	
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
		LOGGER.debug("Starting task "+taskName);
		Task t = getTask(taskName);
		TaskResult tr = t.runTask();
		String next = transitionDefinition.getNextTransitionName(taskName,tr);
		if(next!=null){
			runTask(next);
		}
		else{
			LOGGER.debug("Workflow finished");
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
			LOGGER.error("Workflow instance error");
		}
	}
	
}
