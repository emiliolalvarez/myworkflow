package com.myworkflow.workflow;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class WorkflowProperties  extends Properties{

	private static final long serialVersionUID = 1L;
	
	private final Logger LOGGER = Logger.getLogger(WorkflowProperties.class);

	public WorkflowProperties(String filename){
		FileInputStream in;
		try {
		   in = new FileInputStream(filename);
		   this.load(in);
		   in.close();
		} catch (Exception e) {
		   LOGGER.error(e.getMessage());
		   System.exit(1);
		}
	}
	
	public int getIntProperty(String name){
		int value = Integer.parseInt(this.getProperty(name, "1").trim());
		return value;
	}
	
	public String getStringProperty(String name){
		return this.getProperty(name,null).trim();
	}

}
