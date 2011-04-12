package com.teevity.cloud.frameworks.awsy.RDS;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;


/**
 * 
 * Use a pool of connection created by TomCat
 * 
 * @author Mathieu Passenaud, Nicolas Fonrose
 *
 */
public class RDSJEE extends RDSConnection{
	
	private RDSJEE(){
		try {
			// Récupération de la source de donnée
			Context initCtx = new InitialContext();
			DataSource ds;
			ds = (DataSource) initCtx.lookup("java:comp/env/jdbc/SageSaaSPool1");
			_conShard1 = ds.getConnection();
			ds = (DataSource) initCtx.lookup("java:comp/env/jdbc/SageSaaSPool2");
			_conShard2 = ds.getConnection();
		} catch (Exception e) {
			_logger.error("BusinessRequestsService singleton instance creation failed", e);
		}
	}
	public static RDSConnection getInstance(){
		__connection = new RDSJEE();
		return __connection;
	}
}
