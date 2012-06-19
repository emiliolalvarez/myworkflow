package com.myworkflow.transition;


public class Transition {
	
	private String name;
	private String result;
	private String nextTransition;
	
	public Transition(String name,String result,String nextTransition){
		this.name = name;
		this.result = result;
		this.nextTransition = nextTransition;
	}
	
	public String getResultCondition() {
		return result;
	}

	public void setResultCondition(String result) {
		this.result = result;
	}

	public String getNextTransition() {
		return nextTransition;
	}

	public void setNextTransition(String nextTransition) {
		this.nextTransition = nextTransition;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName(){
		return this.name;
	}
}
