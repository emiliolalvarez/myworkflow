package com.myworkflow.workflow;

import com.myworkflow.task.TaskAsync;
import com.myworkflow.task.TaskAsyncResult;

public class CallbackListener extends Thread {
	
	private WorkflowApplicationContext context;
	private String queueName;
	private boolean isRunning = false;
	
	public CallbackListener(WorkflowApplicationContext context, String queueName){
		this.context = context;
		this.queueName = queueName;
	}
	
	public void run(){
		System.out.println("Callback listener ["+queueName+"] started...");
		isRunning = true;
		while(isRunning){
			try {
				TaskAsync t = null;
				TaskAsyncResult r = context.getTaskAsyncResult(queueName);
				if(r!=null){
					t = r.getTaskAsync();
					t.notifyAsyncTaskFinalization(r);
					System.out.println("Task is done in ["+queueName+"] queue => "+t.getCurrentTask().getWorkflow().getName());
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
				if(e instanceof InterruptedException){
					isRunning=false;
				}
			}
		}
	}
	
	public void setIsRunning(boolean running){
		isRunning = running;
	}
}
