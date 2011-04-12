package com.teevity.cloud.frameworks.saasy.clients;



/**
 * Représente une entreprise cliente de la solution Saas. On parle aussi 
 * de la notion de Tenant (architecture multi-tenants)
 * 
 * Plusieurs utilisateurs de cette entreprise peuvent probablement se connecter
 * au service SaaS. Attention à ne pas confondre Client et Utilisateurs du service.
 * 
 * @author Mathieu Passenaud, Nicolas Fonrose
 *
 */
public class Client {

	//private static Logger _logger = Logger.getLogger(Client.class);
	
	
	private int id;
	private String name;
	
	/**
	 * Constructor
	 * 
	 * @param i
	 * @param name
	 */
	public Client(int i, String name) {
		super();
		this.id = i;
		this.name = name;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
}
