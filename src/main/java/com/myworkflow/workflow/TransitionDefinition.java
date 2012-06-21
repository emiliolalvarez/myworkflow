package com.myworkflow.workflow;

import java.util.ArrayList;
import java.util.List;

import com.myworkflow.TaskResult;
import com.myworkflow.transition.Transition;


public class TransitionDefinition implements ITransitionDefinition {
	
	private boolean hasAnyTransitions = false;
	
	private String initialTransition = null;
	
	private List<Transition> transitions = new ArrayList<Transition>();
	
	public TransitionDefinition(){
		
	}
	
	public boolean hasAnyTransitions(){
		return hasAnyTransitions;
	}
	
	public void addTransition(Transition transition){
		if(!hasAnyTransitions){
			hasAnyTransitions = true;
			initialTransition = transition.getName();
		}
		transitions.add(transition);
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
	
}