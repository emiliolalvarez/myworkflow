package com.workflow.requestlistener;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;

import com.workflow.workflow.WorkflowDefinition;

public class RequestListener extends Thread {
	
	private ServerSocketChannel server;
	private Selector selector;
	private int port;
	private String host;
	private WorkflowDefinition wd;
	
	
	
	public RequestListener(String host, int port, WorkflowDefinition wd){
		this.port = port;
		this.host = host;
		this.wd = wd;
	}
	
	
	public void run(){
		try{
			// Create the server socket channel
			server = ServerSocketChannel.open();
			// nonblocking I/O
			server.configureBlocking(false);
			// host-port 8000
			server.socket().bind(new java.net.InetSocketAddress(host,port));
			System.out.println("Server ["+host+"] listening on port "+port);
			// Create the selector
			selector = Selector.open();
			// Recording server to selector (type OP_ACCEPT)
			server.register(selector,SelectionKey.OP_ACCEPT);
		
			// Infinite server loop
			while(true) {
				
			  // Waiting for events
			  selector.select();
			  // Get keys
			  Set<SelectionKey> keys = selector.selectedKeys();
			  Iterator<SelectionKey> i = keys.iterator();
			  	
			  // For each keys...
			  while(i.hasNext()) {
				SelectionKey key = (SelectionKey) i.next();
				try{  
	
				    // Remove the current key
				    i.remove();
				    if(!key.isValid()){
				    	continue;
				    }
				    // if isAccetable = true
				    // then a client required a connection
				    else if (key.isAcceptable()) {
				    	this.onAccpetableKey();
				    }
				    
	
				    // if isReadable = true
				    // then the server is ready to read 
				    else if (key.isReadable()) {
				      this.onReadableKey(key);
				      
				    }
				    
				    // if isWritable = true
				    // then the server is ready to write
				    else if(key.isWritable()){
				    	this.onWritableKey(key);
				    }
				 }   
				 catch(Exception e){
					 e.printStackTrace();
					 key.channel().close();
				 }
			  
			  }
			  sleep(100);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void onAccpetableKey() throws IOException{
	  // get client socket channel
	  SocketChannel client = server.accept();
	  // Non Blocking I/O
	  client.configureBlocking(false);
	  // recording to the selector (reading)
	  client.register(selector, SelectionKey.OP_READ);
    }
	
	private void onReadableKey(SelectionKey key) throws IOException{
		SocketChannel client = (SocketChannel) key.channel();

		// Read byte coming from the client
		int BUFFER_SIZE = 32;
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		try {
		    client.read(buffer);
		}
		catch (Exception e) {
		    // client is no longer active
		    e.printStackTrace();
		}
		
		// Show bytes on the console
		buffer.flip();
		Charset charset=Charset.forName("UTF-8");
		CharsetDecoder decoder = charset.newDecoder();
		CharBuffer charBuffer = decoder.decode(buffer);
		System.out.print(charBuffer.toString());
		this.processRequest(key, client, charBuffer.toString());
		
	}
	
	private void processRequest(SelectionKey key, SocketChannel client, String request) throws IOException{
		client.register(selector, SelectionKey.OP_WRITE);
		System.out.println("Request: "+request.trim().toLowerCase());
		try {
			wd.getRequestQueue().put(request.trim());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Request queue size: "+wd.getRequestQueue().size());
		client.register(selector, SelectionKey.OP_READ);
//		//if(request.toLowerCase().equals("summary")){
//			String[] summary = wd.getSummary().split(";");
//			for(String s : summary){
//				this.writeResponse(key, client, s + "\n");
//			}
//		//}
	}
	
	private void writeResponse(SelectionKey key, SocketChannel client,String message) 
	throws IOException{
		
		key.interestOps(SelectionKey.OP_WRITE);
		
        ByteBuffer buf = ByteBuffer.wrap( message.getBytes() );

        client.write( buf );
        
        key.interestOps(SelectionKey.OP_READ);
        //key.channel().close();
	}
	
	private void onWritableKey(SelectionKey key) throws IOException{
//		SocketChannel client = (SocketChannel) key.channel();
//
//        String message = "What is your name? ";
//
//        ByteBuffer buf = ByteBuffer.wrap( message.getBytes() );
//
//        client.write( buf );
   
	} 
}
