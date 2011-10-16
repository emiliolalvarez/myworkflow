package com.workflow.main;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class WorkflowClient extends Thread {
	
	public void run(){
		try {
			Socket client = new Socket("localhost", 8000);
			int requests = 10000;
			String message;
			while(requests>0){
				message = "message_"+requests+"\n";
				client.getOutputStream().write(message.getBytes());
				requests--;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				
	}
}
