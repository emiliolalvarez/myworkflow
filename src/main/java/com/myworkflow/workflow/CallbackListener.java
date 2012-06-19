package com.myworkflow.workflow;

import java.util.concurrent.CompletionService;
import java.util.concurrent.Future;

import com.myworkflow.task.TaskAsync;
import com.myworkflow.task.TaskAsyncResult;

public class CallbackListener extends Thread {
	
	private WorkflowDefinitionContext workflowDefinitionContext;
	private String queueName;
	private boolean isRunning = false;
	
	public CallbackListener(WorkflowDefinitionContext context, String queueName){
		this.workflowDefinitionContext = context;
		this.queueName = queueName;
	}
	
	public void run(){
		System.out.println("Callback listener ["+queueName+"] started...");
		isRunning = true;
		while(isRunning){
			try {
				CompletionService<TaskAsyncResult> cs = workflowDefinitionContext.getCompletionService(queueName);
				Future<TaskAsyncResult> f  = cs.take();
				TaskAsync t = null;
				
				TaskAsyncResult r = f.get();
				t = r.getTaskAsync();
				t.notifyAsyncTaskFinalization(r);
				System.out.println("Task is done in ["+queueName+"] queue => "+t.getCurrentTask().getWorkflow().getName());
			} 
			catch (Exception e) {
				isRunning=false;
			}
		}
	}
	
	public void setIsRunning(boolean running){
		isRunning = running;
	}
}
