package com.workflow.jetty.httpserver;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/goodbye")
public class GoodbyeServlet{
	@GET
	@Produces( { MediaType.TEXT_PLAIN })
	public String sayGoodbye(){
		System.out.println("Goodbye!");
		return "Goodbye";
	}
}
