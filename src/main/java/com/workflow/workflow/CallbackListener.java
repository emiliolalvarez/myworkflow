package com.workflow.workflow;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import com.workflow.task.TaskAsync;

public class CallbackListener extends Thread {
	
	private WorkflowDefinition workflowDefinition;
	private String queueName;
	
	public CallbackListener(WorkflowDefinition wd, String queueName){
		this.workflowDefinition = wd;
		this.queueName = queueName;
	}
	
	public void run(){
		System.out.println("Image callback listener started...");
		while(true){
			try {
				LinkedBlockingQueue<Future<TaskAsync>> q = workflowDefinition.getCallbackQueue(queueName);
				
				Future<TaskAsync> f  = q.take();
				
				if(!f.isDone()){
					//System.out.println("Re-enqueuing task in ["+queueName+"] queue");
					q.put(f);
					Thread.sleep(100);
					continue;
				}
				else{
					System.out.println("Task is done in ["+queueName+"] queue");
					TaskAsync t = null;
					try {
						t = f.get();
						t.notifyAsyncTaskFinalization();
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
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
