package com.myworkflow.workflow;

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
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import com.myworkflow.task.TaskAsync;
import com.myworkflow.task.TaskAsyncResult;
import com.myworkflow.task.TaskCallable;
import com.myworkflow.utils.WorkflowThreadFactory;

public class WorkflowDefinitionContext {
	
	protected Map<String,LinkedBlockingQueue<Future<TaskAsync>>> callbacks = new HashMap<String,LinkedBlockingQueue<Future<TaskAsync>>>();
	
	protected Map<String,ExecutorService> executors = new HashMap<String,ExecutorService>();
	protected Map<String,LinkedList<Future<?>>> executorsFutures = new HashMap<String,LinkedList<Future<?>>>();
	
	protected Map<String,CompletionService<TaskAsyncResult>> completion = new HashMap<String,CompletionService<TaskAsyncResult>>();
	
	protected LinkedList<CallbackListener> callbackListeners = new LinkedList<CallbackListener>();
	
	protected WorkflowProperties properties;
	
	final AtomicInteger threadNumber = new AtomicInteger(1);
	
	public WorkflowDefinitionContext(String propertiesFileName){
		properties = new WorkflowProperties(propertiesFileName);
	}
	
	public WorkflowProperties getWorkflowDefintionProperties(){
		return properties;
	}
	
	public synchronized void addCallbackListener(String queueName,int poolSizeCallback){
		for(int i=0;i<poolSizeCallback;i++){
			CallbackListener cl = new CallbackListener(this, queueName);
			cl.setName(queueName+"-callback-thread-"+this.threadNumber.getAndIncrement());
			callbackListeners.add(cl);
			cl.start();
		}
	}
	
	private  ExecutorService getExecutor(String name){
		if(executors.get(name)==null){
			int poolSize = properties.getIntProperty("executor.size."+name);
			System.out.println("Executor "+name+" pool size: "+poolSize);
			WorkflowThreadFactory f = new WorkflowThreadFactory(name);
			executors.put(name, Executors.newFixedThreadPool(poolSize,f));
		}
		return executors.get(name);
	}
	
	private LinkedList<Future<?>> getExecutorFuture(String name){
		if(executorsFutures.get(name)==null){
			executorsFutures.put(name, new LinkedList<Future<?>>());
		}
		return executorsFutures.get(name);
	}
	
	public void queueExecutorTask(String executorName,Runnable task){
		ExecutorService es = getExecutor(executorName);
		es.submit(task);
	}
	
	public CompletionService<TaskAsyncResult> getCompletionService(String name){
		if(completion.get(name)==null){
			
			int poolSizeExecutor = properties.getIntProperty("completion.executor.size."+name);
			int poolSizeCallback = properties.getIntProperty("completion.callback.size."+name);
			System.out.println("Completion executor "+name+" pool size: "+poolSizeExecutor);
			System.out.println("Completion callback "+name+" pool size: "+poolSizeCallback);
			WorkflowThreadFactory f = new WorkflowThreadFactory("completion-"+name);
			ExecutorService es = Executors.newFixedThreadPool(poolSizeExecutor,f);
			completion.put(name, new ExecutorCompletionService<TaskAsyncResult>(es));
			executors.put("completion-executor-"+name, es);
			this.addCallbackListener(name,poolSizeCallback);
		}
		return completion.get(name);
	}

	public synchronized void queueAsyncTask(String queueName, TaskCallable callable){
		CompletionService<TaskAsyncResult> completion = this.getCompletionService(queueName);
		completion.submit(callable);
	}
	
	protected synchronized void clearCallbacksQueue(){
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
		callbacks.clear();
	}
	
	protected synchronized void clearExecutors(){
		Iterator<String> it = executors.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			ExecutorService es = executors.get(key);
			ThreadPoolExecutor tpe = (ThreadPoolExecutor)es;
			es.shutdownNow();
			System.out.println("Active threads in "+key+" pool: "+tpe.getActiveCount());
		}
		executors.clear();
	}
	
	protected synchronized void clearCallbackListeners(){
		Iterator<CallbackListener> it = callbackListeners.iterator();
		while(it.hasNext()){
			CallbackListener cl = it.next();
			cl.setIsRunning(false);
			cl.interrupt();
		}
		callbackListeners.clear();
	}
	
	protected synchronized void clearCompletionServices(){
		Iterator<String> it = completion.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			CompletionService<TaskAsyncResult> cs = completion.get(key);
			while(true){
				Future<TaskAsyncResult> f = cs.poll();
				if(f==null){
					break;
				}
				f.cancel(true);
			}
		}
		completion.clear();
	}
	
	public void finish(){
		System.out.println("============Finishing context============");
		clearCallbackListeners();
		clearCallbacksQueue();
		clearCompletionServices();
		clearExecutors();
	}
	
}
