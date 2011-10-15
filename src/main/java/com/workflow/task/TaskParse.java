package com.workflow.task;

import com.workflow.workflow.Workflow;


public class TaskParse extends Task {

	public TaskParse(Workflow w){
		super(w);
	}
	
	@Override
	public TaskResult runTask() {
		
//		for(int i = 0; i<5; i++){
//			//System.out.println("Parse iteration: "+i);
//			try {
//				long delay = Math.round( Math.random() * 1400 );
//				Thread.sleep(delay);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		
		return new TaskResult("success","Parse ok!");
	}

}
