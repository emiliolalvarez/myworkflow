package com.workflow.jetty.httpserver;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class HelloServlet {
	
	@GET
	@Produces( { MediaType.TEXT_PLAIN })
	public String sayHello(){
		System.out.println("Welcome!");
		return "Welcome";
	}

}
