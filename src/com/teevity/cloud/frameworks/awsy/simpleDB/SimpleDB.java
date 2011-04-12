package com.teevity.cloud.frameworks.awsy.simpleDB;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.amazonaws.services.simpledb.model.ListDomainsResult;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;

/**
 * This Class wrap a table of a simple DB instance.
 * 
 * The constructor is used to connect to the AWS account.
 * 
 * <table border=1>
 * <tr>
 * <td>TABLE</td><td>Attribute1</td><td>Attribute2</td>
 * </tr>
 * <tr>
 * <td>Item</td><td>value1</td><td>value2</td>
 * </tr>
 * </table><br />
 * An attribute is created with a list of items witch can be replaced. That means only one operation
 * is needed to replace or to add an attribute.
 * 
 * 
 * @author Mathieu Passenaud, Nicolas Fonrose
 *
 */
public class SimpleDB {

	private Logger _logger = Logger.getLogger(SimpleDB.class);

	private String _domainName;
	private String _parametersFile;
	private AmazonSimpleDB _simpleDBConnection;

	/**
	 * Connect to the domain. If the domain does not exists and createDomain = true, the 
	 * domain will be created
	 * 
	 * @param domainName
	 * @param parameters
	 * @param createDomainIfNotExist
	 * @throws Exception
	 */
	public SimpleDB(String domainName, String parameters, boolean createDomainIfNotExist) throws Exception{
		this._domainName = domainName;
		this._parametersFile = parameters;
		this.initializeConnection(createDomainIfNotExist);
	}

	/**
	 * 
	 * @param createDomain
	 * @throws Exception
	 */
	private void initializeConnection(boolean createDomain) throws Exception {
		try{
			InputStream is = SimpleDB.class.getClassLoader().getResourceAsStream( this._parametersFile );

			// Connect to amazon account and set the SimpleDB endpoint to the EU-WEST-1 region
			_simpleDBConnection = new AmazonSimpleDBClient(new PropertiesCredentials(is));
			_simpleDBConnection.setEndpoint("sdb.eu-west-1.amazonaws.com");

			_logger.info(String.format("Connection to the AWS account for domain '%s' initialized", _domainName));

			ListDomainsResult listDomainsResult = _simpleDBConnection.listDomains();
			ArrayList<String> domains = (ArrayList<String>) listDomainsResult.getDomainNames();
			for(String domain : domains){
				if(domain.equals(this._domainName)){
					_logger.info("Domain connection succesful");
					return;
				}
			}
			if(createDomain){
				_simpleDBConnection.createDomain(new CreateDomainRequest().withDomainName(this._domainName));
				_logger.info("Domain created succesfully");
			}else{
				String message = "Domain name unavailable";
				_logger.error(message);
				throw new Exception(message);
			}
		} catch (AmazonServiceException ase) {
			String message = String.format("Unable to create or retrieve SimpleDB Domain '%s'", _domainName);
			_logger.error(message, ase);
			throw new Exception(message, ase);
		} catch (Exception e) {
			String message = String.format("Unable to connect to the Amazon account with these credentials [%s]", _parametersFile); 
			_logger.error(message, e);
			throw new Exception(message, e);
		} 
	}
	
	/**
	 * is like an insert in SQL with a creation if the attribute does not exist
	 * <b>BE CARREFULL :</b> Each row must have a maximum size of 10Kb
	 * @param values
	 * @throws Exception
	 */
	public void addItem(String itemName,List<String> attributes, List<String> values) throws Exception{
		ArrayList<ReplaceableAttribute> replaceableAttributeList = new ArrayList<ReplaceableAttribute>();
		for(int i=0; i<values.size(); i++){
			replaceableAttributeList.add(new ReplaceableAttribute(attributes.get(i), values.get(i), true));
		}
		PutAttributesRequest request = new PutAttributesRequest(_domainName, itemName, replaceableAttributeList);
		_simpleDBConnection.putAttributes(request);
	}	
	
	/**
	 * <b>BE CARREFULL :</b> Response limited at 1MB
	 * 
	 * it is a consistent read, that means most recent data will be returned.
	 * 
	 * Example : select attributes from domain where...
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public SelectResult executeSelect(String request) throws Exception{
		SelectResult result = null;
		try{
			result = _simpleDBConnection.select(new SelectRequest(request));
			return result;
		} catch (Exception ex) {	
			_logger.error(String.format("Error while executing select %s", request), ex);
			throw ex;
		}
	}
	
	/**
	 * Delete the domain
	 * <b>All datas in the domain will be deleted. Can not be undone</b>
	 * 
	 */
	public void deleteDomain(){
		DeleteDomainRequest request = new DeleteDomainRequest(this._domainName);
		_simpleDBConnection.deleteDomain(request);
	}
}