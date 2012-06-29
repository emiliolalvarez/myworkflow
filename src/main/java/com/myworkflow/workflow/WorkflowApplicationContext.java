package com.myworkflow.workflow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.myworkflow.main.Configuration;
import com.myworkflow.task.TaskAsync;
import com.myworkflow.task.TaskAsyncResult;
import com.myworkflow.task.TaskCallable;
import com.myworkflow.utils.WorkflowThreadFactory;

public class WorkflowApplicationContext {
	
	final AtomicInteger threadNumber = new AtomicInteger(1);
	
	final AtomicInteger instanceCount = new AtomicInteger(1);
	
	protected Map<String,LinkedBlockingQueue<Future<TaskAsync>>> callbacks = new HashMap<String,LinkedBlockingQueue<Future<TaskAsync>>>();
	
	protected Map<String,ExecutorService> executors = new HashMap<String,ExecutorService>();
	
	protected Map<String,CompletionService<TaskAsyncResult>> completion = new HashMap<String,CompletionService<TaskAsyncResult>>();
	
	protected LinkedList<CallbackListener> callbackListeners = new LinkedList<CallbackListener>();
	
	protected Configuration configuration;
	
	private TransitionDefinition transition;
	
	private Map<Workflow,String> instances = new HashMap<Workflow,String>();
	
	private List<WorkflowObserver> observers = new LinkedList<WorkflowObserver>();
	
	private final Logger LOGGER = Logger.getLogger(WorkflowApplicationContext.class);
	
	public TransitionDefinition getTransitionDefinition(){
		return transition;
	}
	
	
	/**
	 * Retrieves a new Workflow object
	 * @param c Class that extends from Workflow.class
	 * @return <T extends Workflow> object 
	 */
	public synchronized <T extends Workflow> T getWorkflowInstance(Class<T> c){

		if(!transition.hasAnyTransitions())return null;

		try {
			T workflow = (T) c.getDeclaredConstructor(WorkflowApplicationContext.class,String.class).newInstance(this,"Workflow_"+(instanceCount.incrementAndGet()));
			this.instances.put(workflow, transition.getFirstTransitionTaskName());
			return workflow;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return null;
	}
	
	protected synchronized void finishWorkflow(Workflow w){
		
		for(WorkflowObserver wo : this.observers){
			wo.notifyFinishedEvent(w);
		}
		
		this.instances.remove(w);
	}
	
	public WorkflowApplicationContext(TransitionDefinition transition,Configuration configuration){
		this.transition = transition;
		this.configuration = configuration;
	}
	
	public synchronized void updateWorkflowStatus(String status, Workflow w){
		this.instances.put(w, status);
	}

	public void subscribe(WorkflowObserver wo){
		this.observers.add(wo);
	}
	
	public Configuration getWorkflowDefintionProperties(){
		return configuration;
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
			int poolSize = configuration.getInt("executor.size."+name);
			WorkflowThreadFactory f = new WorkflowThreadFactory(name);
			executors.put(name, Executors.newFixedThreadPool(poolSize,f));
		}
		return executors.get(name);
	}
	
	public void queueExecutorTask(String executorName,Runnable task){
		ExecutorService es = getExecutor(executorName);
		es.submit(task);
	}
	
	private CompletionService<TaskAsyncResult> getCompletionService(String name){
		if(completion.get(name)==null){
			
			int poolSizeExecutor = configuration.getInt("completion.executor.size."+name);
			int poolSizeCallback = configuration.getInt("completion.callback.size."+name);
			LOGGER.info("ASYNC QUEUE CREATED: "+name);
			LOGGER.info("Completion executor "+name+" pool size: "+poolSizeExecutor);
			LOGGER.info("Completion callback "+name+" pool size: "+poolSizeCallback);
			WorkflowThreadFactory f = new WorkflowThreadFactory("completion-"+name);
			ExecutorService es = Executors.newFixedThreadPool(poolSizeExecutor,f);
			completion.put(name, new ExecutorCompletionService<TaskAsyncResult>(es));
			executors.put("completion-executor-"+name, es);
			this.addCallbackListener(name,poolSizeCallback);
		}
		return completion.get(name);
	}
	/**
	 * Gets a TaskAsyncResult object from the given async task results queue
	 * @param String queueName
	 * @return TaskAsyncResult
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public TaskAsyncResult getTaskAsyncResult (String queueName) throws InterruptedException, ExecutionException{
		CompletionService<TaskAsyncResult> cs = this.getCompletionService(queueName);
		Future<TaskAsyncResult> f  = cs.take();
		TaskAsyncResult r = f.get();
		return r;
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
			LOGGER.info("Active threads in "+key+" pool: "+tpe.getActiveCount());
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
		LOGGER.info("============Finishing context============");
		clearCallbackListeners();
		clearCallbacksQueue();
		clearCompletionServices();
		clearExecutors();
	}
	
}
