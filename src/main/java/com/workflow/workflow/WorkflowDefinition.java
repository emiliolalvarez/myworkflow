package com.workflow.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import com.workflow.netty.discardserver.DiscardServer;
import com.workflow.task.TaskAsync;
import com.workflow.task.TaskCallable;
import com.workflow.task.TaskResult;
import com.workflow.transition.Transition;


public class WorkflowDefinition {
	
	private static boolean hasAnyTransitions = false;
	
	private static String initialTransition = null;
	
	private static int instanceCount = 0;
	
	private List<Transition> transitions = new ArrayList<Transition>();
	
	private Map<Workflow,String> instances = new HashMap<Workflow,String>();
	
	private List<WorkflowObserver> observers = new LinkedList<WorkflowObserver>();
	
	private Map<String,LinkedBlockingQueue<Future<TaskAsync>>> callbacks = new HashMap<String,LinkedBlockingQueue<Future<TaskAsync>>>();
	
	private Map<String,ExecutorService> executors = new HashMap<String,ExecutorService>();
	
	private BlockingQueue<String> requestQueue = new LinkedBlockingQueue<String>();
	
	private DiscardServer requestListener;
	
	private WorkflowContext context;
	
	public WorkflowDefinition(WorkflowContext context){
		
		this.context = context;
		this.requestListener = new DiscardServer(this,8000);
		this.requestListener.startServer();
	}
	
	public void addTransition(Transition transition){
		if(!hasAnyTransitions){
			hasAnyTransitions = true;
			initialTransition = transition.getName();
		}
		transitions.add(transition);
	}
	
	public WorkflowContext getWorkflowContext(){
		return this.context;
	}
	
	public Transition getTransition(String transitionName){
		Transition transition = null;
		for(Transition t : transitions){
			if(t.getName().equals(transitionName)){
				transition = t;
				break;
			}
		}
		return transition;
	}
	
	public String getFirstTransitionTaskName(){
		return initialTransition;
	}
	
	public String getNextTransitionName(String currentTransition, TaskResult tr){
		String transition=null;
		for(Transition t:transitions){
			if(t.getResultCondition().equals(tr.getStatus()) 
					&& t.getName().equals(currentTransition)){
				transition = t.getNextTransition();
				break;
			}
		}
		return transition;
	}
	
	public synchronized void finishWorkflow(Workflow w){
		for(WorkflowObserver wo : this.observers){
			wo.notifyFinishedEvent(w);
		}
		this.instances.remove(w);
	}

	public synchronized Workflow getWorkflowInstance(){
		if(!hasAnyTransitions)return null;
		instanceCount++;
		Workflow workflow = new Workflow(this,"Workflow_"+(instanceCount));
		this.instances.put(workflow, initialTransition);
		return workflow;
		
	}
	
	public synchronized void updateWorkflowStatus(String status, Workflow w){
		this.instances.put(w, status);
	}
	
	public synchronized String getSummary(){
		Set<Workflow> keys = instances.keySet();
		
		Iterator<Workflow> it = keys.iterator();
		StringBuffer sb = new StringBuffer();
		while(it.hasNext()){
			Workflow w = it.next();
			sb.append(";"+" Status: " +instances.get(w));
		}
		return sb.toString();
	}
	
	public void subscribe(WorkflowObserver wo){
		this.observers.add(wo);
	}
	
	public BlockingQueue<String> getRequestQueue(){
		return this.requestQueue;
	}
	
	
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