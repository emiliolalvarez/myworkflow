package com.myworkflow.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.myworkflow.TaskResult;
import com.myworkflow.transition.Transition;


public class WorkflowDefinition {
	
	private static boolean hasAnyTransitions = false;
	
	private static String initialTransition = null;
	
	private static int instanceCount = 0;
	
	private List<Transition> transitions = new ArrayList<Transition>();
	
	private Map<Workflow,String> instances = new HashMap<Workflow,String>();
	
	private List<WorkflowObserver> observers = new LinkedList<WorkflowObserver>();
	
	private WorkflowDefinitionContext context;
	
	public WorkflowDefinition( WorkflowDefinitionContext context){
		
		this.context = context;
		
	}
	
	public void addTransition(Transition transition){
		if(!hasAnyTransitions){
			hasAnyTransitions = true;
			initialTransition = transition.getName();
		}
		transitions.add(transition);
	}
	
	public WorkflowDefinitionContext getWorkflowDefinitionContext(){
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
	/**
	 * Retrieves a new Workflow object
	 * @param c Class that extends from Workflow.class
	 * @return <T extends Workflow> object 
	 */
	public synchronized <T extends Workflow> T getWorkflowInstance(Class<T> c){
		if(!hasAnyTransitions)return null;
		instanceCount++;

		try {
			T workflow = (T) c.getDeclaredConstructor(WorkflowDefinition.class,String.class).newInstance(this,"Workflow_"+(instanceCount));
			this.instances.put(workflow, initialTransition);
			return workflow;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
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
}