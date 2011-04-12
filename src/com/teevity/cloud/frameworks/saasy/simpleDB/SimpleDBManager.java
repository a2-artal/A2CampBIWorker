package com.teevity.cloud.frameworks.saasy.simpleDB;

import com.teevity.cloud.frameworks.awsy.simpleDB.SimpleDB;



/**
 * 
 * @author Mathieu Passenaud, Nicolas Fonrose
 *
 */
public class SimpleDBManager {
	public static final String CLIENTSDOMAIN="clients";
	public static final String IMPORTTASKSTATUSDOMAIN="importTaskStatus";

	//private static Logger _logger = Logger.getLogger(SimpleDBManager.class);
	
	private static final SimpleDBManager __simpleDBManager = new SimpleDBManager();
	
	private SimpleDBManager(){
		super();
	}
	
	/**
	 * Singleton implementation
	 * @return
	 */
	public static SimpleDBManager getInstance(){
		return __simpleDBManager;
	}
	
	/**
	 * The domain name represents the table witch we're looking for
	 * 
	 * @param domainName
	 * @param createDomainIfNotExist
	 * @return
	 * @throws Exception
	 */
	public SimpleDB getSimpleDB(String domainName, boolean createDomainIfNotExist) throws Exception{
		return new SimpleDB(domainName, "AwsCredentials.properties", createDomainIfNotExist);
	}
}
