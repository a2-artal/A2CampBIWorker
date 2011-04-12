package com.teevity.cloud.frameworks.saasy.clients;


import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectResult;
import com.teevity.cloud.frameworks.saasy.RDS.RDSManager;
import com.teevity.cloud.frameworks.saasy.simpleDB.SimpleDBManager;


/**
 * 
 * @author Mathieu Passenaud, Nicolas Fonrose
 *
 */
public class ClientManager {
	
	private static Logger _logger = Logger.getLogger(ClientManager.class);
	
	private static final ClientManager __instance = new ClientManager();
	
	/**
	 * Singleton implementation
	 */
	public static ClientManager getInstance() {
		return __instance;
	}

	/**
	 * 
	 * @param clientId
	 * @param subscriptionLevel The level of subscription of the client (this parameter 
	 * 							helps accommodate situations where several subscriptions 
	 * 							are offered by the 'SaaS service provider')
	 * @return
	 */
	public boolean isClientSubscriptionValid(int clientId, String subscriptionLevel) {
		
		// connect to simpleDB and execute Select clientId from customers where id_client = clientId
		
		return true;
	}
	
	/**
	 * 
	 * @param clientId
	 */
	public Client getClientInfoForUser(String userId) {
		// TODO - Aller chercher dans la base SimpleDB les infos sur le client (ie la société cliente) 
		//        auquel le user appartient (on cherche d'abord le clientId associé au user puis ensuite les
		// 	      informations sur la société cliente).
		return null;
	}
	
	/**
	 * A valid subscription is a payed an validated inscription
	 * @return
	 */
	public List<Client> getClientsWithAValidSubscription() throws Exception{
		ArrayList<Client> clients = new ArrayList<Client>();
		SelectResult result = null;
		
		SimpleDBManager simpleDBManager = SimpleDBManager.getInstance();
		
		try{
			result = simpleDBManager.getSimpleDB("clients", true).executeSelect("Select clientID, name from clients");
			_logger.debug("SimpleDB - select clients result size => " + result.getItems().size());
			_logger.debug("SimpleDB - select clients - number of attributes item 0 => " + result.getItems().get(0).getAttributes());
			
			for(Item item : result.getItems()){
				// TODO Récupérer les valeurs de champs par nom et pas par position
				int clientId = new Integer(item.getAttributes().get(1).getValue());
				String name = item.getAttributes().get(0).getValue();
				clients.add(new Client(clientId, name));
			}
			
			return clients;
		}catch(Exception ex){
			_logger.error("unable to get clients list from simpleDB", ex);
			throw ex;
		}
	}

	/**
	 * Clients for which we store data
	 * @return
	 */
	public List<Client> getClientsForWitchWeHaveData()throws Exception{
		ArrayList<Client> clientsFromRDS = new ArrayList<Client>();
		
		
		clientsFromRDS = (ArrayList<Client>) RDSManager.getInstance().getClientList();
		
		
		return clientsFromRDS;
	}
	
	/**
	 * number of clients for which we store data
	 * @return
	 */
	public int getNumberOfClientsForWitchWeHaveData()throws Exception{
		
		return RDSManager.getInstance().getNumberOfClientsForWitchWeHaveData();
	}
	
	/**
	 * Save the client into the Simple DB database
	 * @param client
	 * @throws Exception
	 */
	public void addClient(Client client){
		SimpleDBManager simpleDBManager = SimpleDBManager.getInstance();
		ArrayList<String> attributes = new ArrayList<String>();
		attributes.add("clientID");
		attributes.add("name");
		ArrayList<String> values = new ArrayList<String>();
		values.add(client.getId()+"");
		values.add(client.getName());
		try{
			simpleDBManager.getSimpleDB("clients", true).addItem(client.getId()+"", attributes, values);
		}catch(Exception ex){
			_logger.error(String.format("Unable to insert client %i into SimpleDB", client.getId()), ex);
		}
	}
}
