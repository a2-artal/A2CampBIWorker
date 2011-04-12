package com.teevity.cloud.frameworks.awsy.RDS;

import org.apache.commons.dbcp.BasicDataSource;


/**
 * 
 * Use a pool of connection created by JDBC Driver
 * 
 * @author Mathieu Passenaud, Nicolas Fonrose
 *
 */
public class RDSJ2se extends RDSConnection{
	
	private RDSJ2se(){
		
		try {
			// Récupération de la source de donnée
			
			//TODO implements datasource pool connection from jdbc driver

			
            
            // Database URI for RDS instances
//			String databaseLogin = "root";
//			String databasePassword = "teevity";
//			String databaseURIShard1 = "jdbc:mysql://pocsaas-shard1.cn5tqb7b0hja.eu-west-1.rds.amazonaws.com:3306/pocsaas";
//			String databaseURIShard2 = "jdbc:mysql://pocsaas-shard2.cn5tqb7b0hja.eu-west-1.rds.amazonaws.com:3306/pocsaas";
            // Database URI for MySQL Teevity server
            String databaseLogin = "teevity";
            String databasePassword = "t33v!ty";
            String databaseURIShard1 = "jdbc:mysql://192.168.0.15:3306/pocsaas_shard1";
            String databaseURIShard2 = "jdbc:mysql://192.168.0.15:3306/pocsaas_shard2";
            
            
            // Create the DataSource for shard 1
            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName("com.mysql.jdbc.Driver");
            ds.setUsername(databaseLogin);
            ds.setPassword(databasePassword);
            ds.setUrl(databaseURIShard1);
            _conShard1 = ds.getConnection();
            
            // Create the DataSource for shard 2
            ds = new BasicDataSource();
            ds.setDriverClassName("com.mysql.jdbc.Driver");
            ds.setUsername(databaseLogin);
            ds.setPassword(databasePassword);
            ds.setUrl(databaseURIShard2);
            _conShard2 = ds.getConnection();
            
		} catch (Exception e) {
			_logger.error("BusinessRequestsService singleton instance creation failed", e);
		}
	}
	
	public static RDSConnection getInstance(){
		__connection = new RDSJ2se();
		return __connection;
	}
}
