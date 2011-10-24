package com.workflow.main;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.workflow.jetty.httpserver.JettyHttpServer;
import com.workflow.netty.discardserver.DiscardServer;
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
		
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
		new DiscardServer(queue,8000).startServer();
		
		MyWorkflowDefinitionContext context = new MyWorkflowDefinitionContext();
		
		WorkflowDefinition wd = new WorkflowDefinition(context);
		
		wd.addTransition(new Transition("verify","success", "parse"));
		wd.addTransition(new Transition("parse","success", "image_download"));
		wd.addTransition(new Transition("image_download","success", "submit"));
		wd.addTransition(new Transition("submit","success",null));
		wd.addTransition(new Transition("error",null,null));
		
		wd.subscribe(new WorkflowObserverClient(wd) );
		
		
		for(int i=0;i<50;i++){
			new WorkflowDefinitionInvoker(wd,queue).start();
		}
		System.out.println("Starting workflow client");
		new WorkflowClient().start();
		new JettyHttpServer(wd).start();
	}

}
