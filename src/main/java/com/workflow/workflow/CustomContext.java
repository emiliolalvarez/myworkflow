package com.workflow.workflow;

public class CustomContext extends WorkflowContext {
	private int parse;
	private int verify;
	private int submit;
	
	public int getParse() {
		return parse;
	}
	public synchronized void increaseParse() {
		this.parse=this.parse+1;
	}
	public int getVerify() {
		return verify;
	}
	public synchronized void increaseVerify() {
		this.verify++;
	}
	public int getSubmit() {
		return submit;
	}
	public synchronized void increaseSubmit() {
		this.submit++;
	}
	
	
}
