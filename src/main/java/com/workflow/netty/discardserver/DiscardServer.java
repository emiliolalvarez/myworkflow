package com.workflow.netty.discardserver;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.workflow.workflow.WorkflowDefinition;

public class DiscardServer {
	
	private WorkflowDefinition workflowDefinition;
	private int port;
	
	public DiscardServer(WorkflowDefinition wd,int port){
		workflowDefinition = wd;
		this.port = port;
	}
	
	public void  startServer(){
		ChannelFactory factory =
				new NioServerSocketChannelFactory(
	                    Executors.newCachedThreadPool(),
	                    Executors.newCachedThreadPool());

	        ServerBootstrap bootstrap = new ServerBootstrap(factory);

	        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
	            public ChannelPipeline getPipeline() {
	                return Channels.pipeline(new DiscardServerHandler(workflowDefinition));
	            }
	        });

	        bootstrap.setOption("child.tcpNoDelay", true);
	        bootstrap.setOption("child.keepAlive", true);

	        bootstrap.bind(new InetSocketAddress(this.port));
	        
	        System.out.println("Listening for request on port: "+this.port);
	}

}