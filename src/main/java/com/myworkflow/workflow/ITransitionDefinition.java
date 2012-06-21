package com.myworkflow.workflow;
import com.myworkflow.TaskResult;
import com.myworkflow.transition.Transition;

public interface ITransitionDefinition {
	
	public boolean hasAnyTransitions();
	
	public void addTransition(Transition transition);
	
	public Transition getTransition(String transitionName);
	
	public String getFirstTransitionTaskName();
	
	public String getNextTransitionName(String currentTransition, TaskResult tr);
}
