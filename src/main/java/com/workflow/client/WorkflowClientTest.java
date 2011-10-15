package com.workflow.client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class WorkflowClientTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		
		Socket socket = new Socket("localhost", 8000);
		socket.getOutputStream().write(new String("asdfasdf\n").getBytes());
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
		wr.write("Test message\r\n");
		wr.write("\r\n");
		 // Send data
	    wr.flush();
		wr.close();
	}

}
