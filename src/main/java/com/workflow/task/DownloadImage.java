package com.workflow.task;

public class DownloadImage extends TaskCallable {

	private String url;
	
	public DownloadImage(String url,TaskAsync t){
		super(t);
		this.url = url;
	}
	
	@Override
	public TaskAsync call(){
		// TODO Auto-generated method stub
		long sleep = Math.round(Math.random() * 8000);
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Image "+url+" downloaded ");
		return  task;
	}

}
