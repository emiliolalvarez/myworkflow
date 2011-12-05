package com.workflow.workflow;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.workflow.task.TaskAsync;

public class CallbackListener extends Thread {
	
	private WorkflowDefinitionContext workflowDefinitionContext;
	private String queueName;
	private boolean isRunning = false;
	
	public CallbackListener(WorkflowDefinitionContext context, String queueName){
		this.workflowDefinitionContext = context;
		this.queueName = queueName;
	}
	
	public void run(){
		System.out.println("Callback listener started...");
		isRunning = true;
		while(isRunning){
			try {
				CompletionService<TaskAsync> cs = workflowDefinitionContext.getCompletionService(queueName);
				
				Future<TaskAsync> f  = cs.take();
				
				TaskAsync t = null;
				
				try {
					t = f.get();
					t.notifyAsyncTaskFinalization();
					System.out.println("Task is done in ["+queueName+"] queue => "+t.getCurrentTask().getWorkflow().getName());
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				catch(ClassCastException e){
					e.printStackTrace();
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setIsRunning(boolean running){
		isRunning = running;
	}
}
