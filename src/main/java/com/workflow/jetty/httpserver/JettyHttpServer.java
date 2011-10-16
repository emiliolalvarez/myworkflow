package com.workflow.jetty.httpserver;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.workflow.workflow.WorkflowDefinition;


public class JettyHttpServer extends Thread {
	
	WorkflowDefinition wd;
	
	public JettyHttpServer(WorkflowDefinition wd){
		this.wd = wd;
	}
	
	public void run()
    {
		ServletHolder sh = new ServletHolder(ServletContainer.class);
		sh.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
        sh.setInitParameter("com.sun.jersey.config.property.packages", "com.workflow.jetty.httpserver");
		
        Server server = new Server(8081);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setAttribute("wdc", wd.getWorkflowDefinitionContext());
		context.addServlet(sh, "/*");

        server.setHandler(context);
        
        
        try {
			server.start();
			server.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
    }
}
