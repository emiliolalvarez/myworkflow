package com.workflow.main;

import com.workflow.jetty.httpserver.JettyHttpServer;
import com.workflow.transition.Transition;
import com.workflow.workflow.WorkflowDefinition;
import com.workflow.workflow.WorkflowDefinitionInvoker;
import com.workflow.workflow.WorkflowObserverClient;

public class TestWorkflow {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		
				
		MyWorkflowDefinitionContext context = new MyWorkflowDefinitionContext();
		
		WorkflowDefinition wd = new WorkflowDefinition(context);
		
		wd.addTransition(new Transition("verify","success", "parse"));
		wd.addTransition(new Transition("parse","success", "image_download"));
		wd.addTransition(new Transition("image_download","success", "submit"));
		wd.addTransition(new Transition("submit","success",null));
		wd.addTransition(new Transition("error",null,null));
		
		wd.subscribe(new WorkflowObserverClient(wd) );
		
		new WorkflowDefinitionInvoker(wd).start();
		new WorkflowClient().start();
		new JettyHttpServer(wd).start();
	}

}
