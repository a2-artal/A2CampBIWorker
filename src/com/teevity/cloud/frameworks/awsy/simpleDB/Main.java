package com.teevity.cloud.frameworks.awsy.simpleDB;

import java.util.ArrayList;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			
			SimpleDB db = new SimpleDB("clients", "AwsCredentials.properties", true);
			ArrayList<String> attributes = new ArrayList<String>();
			attributes.add("clientID");
			attributes.add("name");
			ArrayList<String> values = new ArrayList<String>();
			values.add("1");
			values.add("Teevity");
			db.addItem("Client1", attributes, values);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
