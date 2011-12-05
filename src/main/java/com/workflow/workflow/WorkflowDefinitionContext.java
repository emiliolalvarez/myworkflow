package com.workflow.workflow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.workflow.task.TaskAsync;
import com.workflow.task.TaskCallable;
import com.workflow.utils.WorkflowThreadFactory;

public class WorkflowDefinitionContext {
	
	protected Map<String,LinkedBlockingQueue<Future<TaskAsync>>> callbacks = new HashMap<String,LinkedBlockingQueue<Future<TaskAsync>>>();
	
	protected Map<String,ExecutorService> executors = new HashMap<String,ExecutorService>();
	
	protected Map<String,CompletionService<TaskAsync>> completion = new HashMap<String,CompletionService<TaskAsync>>();
	
	protected LinkedList<CallbackListener> callbackListeners = new LinkedList<CallbackListener>();
	
	final AtomicInteger threadNumber = new AtomicInteger(1);
	
	public synchronized void addCallbackListener(String name){
		CallbackListener cl = new CallbackListener(this, name);
		cl.setName(name+"-callback-thread-"+this.threadNumber.getAndIncrement());
		callbackListeners.add(cl);
		cl.start();
	}
	
	public synchronized LinkedBlockingQueue<Future<TaskAsync>> getCallbackQueue(String name){
		if(callbacks.get(name)==null){
			callbacks.put(name, new LinkedBlockingQueue<Future<TaskAsync>>());
			addCallbackListener(name);
		}
		return (LinkedBlockingQueue<Future<TaskAsync>>)callbacks.get(name);
	}
	
	public synchronized ExecutorService getExecutor(String name){
		if(executors.get(name)==null){
			WorkflowThreadFactory f = new WorkflowThreadFactory(name);
			executors.put(name, Executors.newFixedThreadPool(50,f));
		}
		return executors.get(name);
	}
	
	public synchronized CompletionService<TaskAsync> getCompletionService(String name){
		if(completion.get(name)==null){
			WorkflowThreadFactory f = new WorkflowThreadFactory("completion-"+name);
			ExecutorService es = Executors.newFixedThreadPool(50,f);
			completion.put(name, new ExecutorCompletionService<TaskAsync>(es));
			executors.put("completion-executor-"+name, es);
			this.addCallbackListener(name);
		}
		return completion.get(name);
	}

	public synchronized void queueAsyncTask(String queueName, TaskCallable callable){
		CompletionService<TaskAsync> completion = this.getCompletionService(queueName);
		completion.submit(callable);
	}
	
	protected void clearCallbacksQueue(){
		Iterator<String> it = callbacks.keySet().iterator();
		while(it.hasNext()){
			LinkedBlockingQueue<Future<TaskAsync>> queue = callbacks.get(it.next());
			Iterator<Future<TaskAsync>> qit = queue.iterator();
			while(qit.hasNext()){
				Future<TaskAsync> f = qit.next();
				f.cancel(true);
			}
			queue.clear();
		}
	}
	
	protected void clearExecutors(){
		Iterator<String> it = executors.keySet().iterator();
		while(it.hasNext()){
			ExecutorService es = executors.get(it.next());
			es.shutdownNow();
		}
		executors.clear();
	}
	
	protected void clearCallbackListeners(){
		Iterator<CallbackListener> it = callbackListeners.iterator();
		while(it.hasNext()){
			CallbackListener cl = it.next();
			cl.setIsRunning(false);
			cl.interrupt();
		}
		callbackListeners.clear();
	}
	
	protected void clearCompletionServices(){
		Iterator<String> it = completion.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			CompletionService<TaskAsync> cs = completion.get(key);
			while(true){
				Future<TaskAsync> f = cs.poll();
				if(f==null){
					break;
				}
				f.cancel(true);
			}
			completion.remove(key);
		}
		
	}
	
	
	public void finish(){
		System.out.println("============Finishing context============");
		clearCallbackListeners();
		clearCallbacksQueue();
		clearExecutors();
		clearCompletionServices();
	}
	
}
