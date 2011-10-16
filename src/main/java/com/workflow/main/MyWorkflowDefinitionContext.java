package com.workflow.main;

import java.util.HashMap;
import java.util.Map;

import com.workflow.workflow.WorkflowDefinitionContext;

public class MyWorkflowDefinitionContext extends WorkflowDefinitionContext{
	
	private Map<String,Integer> stats = new HashMap<String, Integer>();
	
	
	public synchronized void increseStat(String stat){
		if(!stats.containsKey(stat)){
			stats.put(stat, 1);
		}
		else{
			stats.put(stat,stats.get(stat)+1);
		}
	}
	
	public synchronized Map<String,Integer> getStats(){
		return stats;
	}
	
	public synchronized Integer getStat(String stat){
		return stats.get(stat);
	}
}
