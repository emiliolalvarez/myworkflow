package com.myworkflow;

import com.myworkflow.task.TaskAsync;
import com.myworkflow.task.TaskAsyncResult;
import com.myworkflow.workflow.Workflow;

public class TaskDownloadImages extends TaskAsync {
	
	private int totalImages = 0;
	private int processedImages = 0;

	public TaskDownloadImages(Workflow w){
		super(w);
		totalImages = 10;
	}
	
	@Override
	public synchronized TaskResult runTask() {
		
		for(int i=0;i<totalImages;i++){
			//Queue async callable tasks
			this.workflow.getContext().queueAsyncTask("images",
					new DownloadImage("http://www.domain.com/image.jpg",this));
			
		}
		
		while(true){
			if(processedImages < totalImages){
				try {
					//System.out.println("Wainting for notification!");
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
			else{
				break;
			}
			
		}
		
		return new TaskResult("success", "Image download");
	}
	
	public synchronized void incrementProcessedImages(){
		processedImages+=1;
		notify();
	}

	@Override
	public void notifyAsyncTaskFinalization(TaskAsyncResult r) {
		this.incrementProcessedImages();
	}
	
}
