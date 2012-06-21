package com.myworkflow.main;

import java.io.FileInputStream;
import java.util.Properties;

public class Configuration {

	private static Properties properties;

	public Configuration() {
		properties = new Properties();
		FileInputStream in;
		try {
			if(System.getProperty("workflow.properties")!=null){
				in = new FileInputStream(System.getProperty("workflow.properties").trim());
			}
			else{
				in = new FileInputStream("workflow.properties");
			}
			properties.load(in);
			in.close();
			System.out.println("Properties loaded!!!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}

	public int getInt(String name) {
		int value = Integer.parseInt(properties.getProperty(name, "1").trim());
		return value;
	}

	public String getString(String name) {
		return properties.getProperty(name, null).trim();
	}
}
