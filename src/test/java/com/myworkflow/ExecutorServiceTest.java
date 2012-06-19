package com.myworkflow;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.myworkflow.utils.WorkflowThreadFactory;

public class ExecutorServiceTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		WorkflowThreadFactory f = new WorkflowThreadFactory("test-executor-service-thread");
		ExecutorService es = Executors.newFixedThreadPool(10,f);
		
		for(int i = 0; i<1;i++){
			es.execute(new Runnable() {
				
				private BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
				
				public void recursiveTask(int i) throws Exception {
					System.out.println("Recursive: "+(i+1) );
						Thread.sleep(100);
					
					if(i<1000)recursiveTask(i+1);
				}
				
				public void run(){
					System.out.println("Thread started...");
					//while(true){
						
						try {
							//queue.take();
							recursiveTask(0);
						} 
						catch (Exception e) {
							if(e instanceof InterruptedException){
								System.out.println("Thread sleep finished...");
								//break;
							}
						}
						System.out.println("end");
					//}
					
				}
			});
		}
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Shoutdown!!!!");
		es.shutdownNow();
		while(true){
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
