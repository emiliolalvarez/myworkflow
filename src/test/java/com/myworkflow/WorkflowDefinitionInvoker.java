package com.myworkflow;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.myworkflow.workflow.Workflow;
import com.myworkflow.workflow.WorkflowDefinition;

public class WorkflowDefinitionInvoker extends Thread {
	
	private WorkflowDefinition wd;
	private ExecutorService executor;
	
	private BlockingQueue<String> queue;
	
	public WorkflowDefinitionInvoker(WorkflowDefinition wd, BlockingQueue<String> queue){
		this.wd = wd;
		this.queue = queue;
		this.executor = Executors.newFixedThreadPool(50);
	
	}
	
	public void run(){
		
		while(true){
			
			try{
				System.out.println("Starting invoker...");
				String req = queue.take();
				
				if(req!=null){
					System.out.println("Taken: "+req);
					Workflow w = wd.getWorkflowInstance(Workflow.class);
					w.addTask("verify",new TaskVerify(w));
					w.addTask("parse",new TaskParse(w));
					w.addTask("image_download", new TaskDownloadImages(w));
					w.addTask("submit", new TaskSubmit(w));
					w.addTask("error", new TaskOnError(w));
					executor.execute(w);
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		
	}

}