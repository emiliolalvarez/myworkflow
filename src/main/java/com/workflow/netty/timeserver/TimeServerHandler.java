package com.workflow.netty.timeserver;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class TimeServerHandler extends SimpleChannelHandler {

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        Channel ch = e.getChannel();
        
        ChannelBuffer time = ChannelBuffers.buffer(4);
        int seconds = new Long(System.currentTimeMillis() / 1000L).intValue();
        time.writeInt(seconds);
        
        
        ChannelFuture f = ch.write(time);
        
        f.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
                Channel ch = future.getChannel();
                ch.close();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}