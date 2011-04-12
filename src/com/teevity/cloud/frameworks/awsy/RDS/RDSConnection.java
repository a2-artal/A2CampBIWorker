package com.teevity.cloud.frameworks.awsy.RDS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * 
 * @author Mathieu Passenaud, Nicolas Fonrose
 * @see RDSJ2se
 * @see RDSJEE
 *
 */
public abstract class RDSConnection{

	protected Logger _logger = Logger.getLogger(RDSConnection.class);
			
			
	protected static RDSConnection __connection;
	protected Connection _conShard1;   // Shard pour idClient de 0 à 5003
	protected Connection _conShard2;   // Shard pour idClient de 5004 à 10000
	
	/**
	 * Singleton implementation
	 * 
	 * @return
	 */
	public static RDSConnection getInstance(){
		return __connection;
	}
	
	/**
	 * This method returns the correct connection depending of the id client given
	 * @param idClient
	 * @return
	 */
	public Connection getConnection(int idClient){
		Connection connection= null;
		if(idClient<5004){
			connection = _conShard1;
		}else{
			connection = _conShard2;
		}
		return connection;
	}
	
	/**
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public PreparedStatement prepareStatement(String sql, int idClient) throws Exception{
		Connection con = getConnection(idClient);
		PreparedStatement statement = null;
		try{
			statement =  con.prepareStatement(sql);
		}catch(Exception ex){
			_logger.error(String.format("Error while preparing statement %s", sql), ex);
			throw ex;
		}
		return statement;
	}
	
	/**
	 * Need to give prepared statement with all parameters already initialized.
	 * 
	 * @param preparedStatement
	 * @return
	 */
	public ResultSet executeQuery(PreparedStatement preparedStatement){
		ResultSet resultSet = null;
		try{
			resultSet = preparedStatement.executeQuery();
		}catch(Exception ex){
			_logger.error("Error while excuting prepared statement", ex);
		}finally{
			try {
				preparedStatement.close();
			} catch (SQLException ex) {
				_logger.error("Unable", ex);
			}
		}
		return resultSet;
	}

}
