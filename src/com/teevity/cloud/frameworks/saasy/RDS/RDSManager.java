package com.teevity.cloud.frameworks.saasy.RDS;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.teevity.cloud.frameworks.awsy.RDS.RDSConnection;
import com.teevity.cloud.frameworks.awsy.RDS.RDSJEE;
import com.teevity.cloud.frameworks.saasy.clients.Client;


/**
 * 
 * This class gives an interface between DataLayer and Model Layer
 * 
 * @author Mathieu Passenaud, Nicolas Fonrose
 *
 */
public class RDSManager {

	private static Logger _logger = Logger.getLogger(RDSManager.class);
	
	private static RDSManager __instance = new RDSManager();
	
	/**
	 * Singleton implementation
	 * @return
	 */
	public static RDSManager getInstance(){
		return __instance;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Client> getClientList() throws Exception{
		ArrayList<Client> clients = new ArrayList<Client>();
		
		RDSConnection connection = RDSJEE.getInstance();
		String statement = "select id_client, customer_name from customer limit 0, 10";
		try {
			ResultSet rs = connection.prepareStatement(statement, 0).executeQuery();
			while(rs.next()){
				
				clients.add(new Client(rs.getInt("id_client"),
				rs.getString("customer_name")));
			}
			return clients;
			//TODO remplir la liste de clients
		} catch (Exception e) {
			_logger.error("unable to get client list from RDS", e);
			throw new Exception(e);
		}
		
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getNumberOfClientsForWitchWeHaveData() throws Exception{
		RDSConnection connection = RDSJEE.getInstance();
		String statement = "select count(distinct(id_client)) from customer";
		try {
			// Number of clients for shard1
			PreparedStatement ps1 = connection.prepareStatement(statement, 0);
			ResultSet rs1 = ps1.executeQuery();
			rs1.next();
			// Number of clients for shard2
			PreparedStatement ps2 = connection.prepareStatement(statement, 5004);
			ResultSet rs2 = ps2.executeQuery();
			rs2.next();
			// Return the sum
			return (rs1.getInt(1)+rs2.getInt(1));
		} catch (Exception e) {
			_logger.error("unable to get client number from RDS", e);
			throw e;
		}
	}
}
