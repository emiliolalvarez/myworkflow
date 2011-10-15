package com.workflow.requestlistener;

public class TestRequestListener {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		RequestListener rl = new RequestListener("localhost",8000,null);
		rl.start();

	}

}
