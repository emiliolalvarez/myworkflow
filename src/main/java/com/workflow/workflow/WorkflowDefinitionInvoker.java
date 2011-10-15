package com.workflow.workflow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.workflow.task.TaskDownloadImages;
import com.workflow.task.TaskOnError;
import com.workflow.task.TaskParse;
import com.workflow.task.TaskSubmit;
import com.workflow.task.TaskVerify;

public class WorkflowDefinitionInvoker extends Thread {
	
	private WorkflowDefinition wd;
	private ExecutorService executor;
	
	public WorkflowDefinitionInvoker(WorkflowDefinition wd){
		
		this.wd = wd;
		this.executor = Executors.newFixedThreadPool(20);
		
	}
	
	public synchronized void run(){
		
		while(true){
			
			try{
				
				String req = wd.getRequestQueue().take();
				
				if(req!=null){
					System.out.println("Taken: "+req);
					Workflow w = wd.getWorkflowInstance();
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
