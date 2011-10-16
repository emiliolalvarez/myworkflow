package com.workflow.jetty.httpserver;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.workflow.main.MyWorkflowDefinitionContext;

@Path("/images_downloaded")
public class ImagesDownloadedServlet  {
	@Context ServletContext context;
	
	@GET
	@Produces( { MediaType.TEXT_PLAIN })
	public String sayGoodbye(){
		MyWorkflowDefinitionContext c = (MyWorkflowDefinitionContext)context.getAttribute("wdc");
		
		return "Donwloaded images: "+c.getStat("downloaded_images");
	}
}
