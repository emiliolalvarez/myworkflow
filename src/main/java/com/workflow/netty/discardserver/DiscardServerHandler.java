package com.workflow.netty.discardserver;

import java.util.concurrent.BlockingQueue;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;


public class DiscardServerHandler extends SimpleChannelHandler {
	private BlockingQueue<String> queue;
	
	public DiscardServerHandler(BlockingQueue<String> q){
		this.queue = q;
	}
	
	@Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		StringBuffer sb = new StringBuffer();
		ChannelBuffer buf = (ChannelBuffer) e.getMessage();
	    while(buf.readable()) {
	    	sb.append((char) buf.readByte());
	    }
	    
	    try {
	    	queue.put(sb.toString());
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        
        Channel ch = e.getChannel();
        ch.close();
    }
}
