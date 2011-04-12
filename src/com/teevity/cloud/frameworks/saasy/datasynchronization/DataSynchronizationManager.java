package com.teevity.cloud.frameworks.saasy.datasynchronization;


public class DataSynchronizationManager {

	private static final DataSynchronizationManager __instance = new DataSynchronizationManager();
	
	/**
	 * Singleton implementation
	 */
	public static DataSynchronizationManager getInstance() {
		return __instance;
	}

	/**
	 * Private constructor for the singleton pattern implementation 
	 */
	private DataSynchronizationManager() {
	}

	/**
	 * Starts a synchronization session with an optional label
	 * @param label
	 * @return ad id associated with this session
	 */
	public long startSynchronizationSession(String label) {
		// TODO - Mettre en oeuvre la notion de session
		return 0;
	}
	
}
