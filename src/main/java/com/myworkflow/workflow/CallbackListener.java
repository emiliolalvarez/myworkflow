package com.myworkflow.workflow;

import org.apache.log4j.Logger;

import com.myworkflow.task.TaskAsync;
import com.myworkflow.task.TaskAsyncResult;

public class CallbackListener extends Thread {

	private WorkflowApplicationContext context;
	private String queueName;
	private boolean isRunning = false;
	private final Logger LOGGER = Logger.getLogger(CallbackListener.class);

	public CallbackListener(WorkflowApplicationContext context, String queueName) {
		this.context = context;
		this.queueName = queueName;
	}

	public void run() {

		LOGGER.info("Callback listener [" + queueName + "] started...");
		isRunning = true;

		while (isRunning) {
			try {
				TaskAsync t = null;
				TaskAsyncResult r = context.getTaskAsyncResult(queueName);
				if (r != null) {
					t = r.getTaskAsync();
					t.notifyAsyncTaskFinalization(r);
					LOGGER.debug("Task is done in [" + queueName
							+ "] queue => "
							+ t.getCurrentTask().getWorkflow().getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (e instanceof InterruptedException) {
					isRunning = false;
				}
			}
		}
	}

	public void setIsRunning(boolean running) {
		isRunning = running;
	}
}
