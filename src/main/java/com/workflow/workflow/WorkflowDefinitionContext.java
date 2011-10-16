package com.workflow.workflow;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import com.workflow.task.TaskAsync;
import com.workflow.task.TaskCallable;

public class WorkflowDefinitionContext {
	
	protected Map<String,LinkedBlockingQueue<Future<TaskAsync>>> callbacks = new HashMap<String,LinkedBlockingQueue<Future<TaskAsync>>>();
	
	protected Map<String,ExecutorService> executors = new HashMap<String,ExecutorService>();
	
	public synchronized LinkedBlockingQueue<Future<TaskAsync>> getCallbackQueue(String name){
		if(callbacks.get(name)==null){
			callbacks.put(name, new LinkedBlockingQueue<Future<TaskAsync>>());
			new CallbackListener(this, name).start();
		}
		return (LinkedBlockingQueue<Future<TaskAsync>>)callbacks.get(name);
	}
	
	public synchronized ExecutorService getExecutor(String name){
		if(executors.get(name)==null){
			executors.put(name, Executors.newFixedThreadPool(20));
		}
		return executors.get(name);
	}
	
	public synchronized void queueAsyncTask(String queueName,TaskCallable callable){
		ExecutorService executor = this.getExecutor(queueName);
		LinkedBlockingQueue<Future<TaskAsync>> callbacks = this.getCallbackQueue(queueName);
		try {
			callbacks.put(executor.submit(callable));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
