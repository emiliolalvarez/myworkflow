package com.workflow.workflow;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import com.workflow.task.TaskAsync;
import com.workflow.task.TaskCallable;

public class WorkflowDefinitionContext {
	
	protected Map<String,LinkedBlockingQueue<Future<TaskAsync>>> callbacks = new HashMap<String,LinkedBlockingQueue<Future<TaskAsync>>>();
	
	protected Map<String,ExecutorService> executors = new HashMap<String,ExecutorService>();
	
	protected Map<String,CompletionService<TaskAsync>> completion = new HashMap<String,CompletionService<TaskAsync>>();
	
	public synchronized LinkedBlockingQueue<Future<TaskAsync>> getCallbackQueue(String name){
		if(callbacks.get(name)==null){
			callbacks.put(name, new LinkedBlockingQueue<Future<TaskAsync>>());
			new CallbackListener(this, name).start();
		}
		return (LinkedBlockingQueue<Future<TaskAsync>>)callbacks.get(name);
	}
	
	public synchronized ExecutorService getExecutor(String name){
		if(executors.get(name)==null){
			executors.put(name, Executors.newFixedThreadPool(50));
		}
		return executors.get(name);
	}
	
	public synchronized CompletionService<TaskAsync> getCompletionService(String name){
		if(completion.get(name)==null){
			completion.put(name, new ExecutorCompletionService<TaskAsync>(Executors.newFixedThreadPool(50)));
			new CallbackListener(this, name).start();
		}
		return completion.get(name);
	}

	public synchronized void queueAsyncTask(String queueName, TaskCallable callable){
		CompletionService<TaskAsync> completion = this.getCompletionService(queueName);
		completion.submit(callable);
	}
	
}
