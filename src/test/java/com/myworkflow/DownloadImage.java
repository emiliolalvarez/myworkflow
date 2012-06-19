package com.myworkflow;

import com.myworkflow.task.TaskAsync;
import com.myworkflow.task.TaskAsyncResult;
import com.myworkflow.task.TaskCallable;

public class DownloadImage extends TaskCallable {
	
	private TaskAsync t;
	private String url;
	
	public DownloadImage(String url,TaskAsync t){
		super(t);
		this.url = url;
		this.t = t;
	}
	
	@Override
	public TaskAsyncResult call(){
		// TODO Auto-generated method stub
		long sleep = Math.round(Math.random() * 2000);
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Image "+url+" downloaded ");
		return  new TaskAsyncResult("String", t, this);
	}

}
