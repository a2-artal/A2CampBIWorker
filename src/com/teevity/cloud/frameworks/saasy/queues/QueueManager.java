package com.teevity.cloud.frameworks.saasy.queues;

import com.teevity.cloud.frameworks.awsy.queues.SQSQueue;

/**
 * 
 * @author Mathieu Passenaud, Nicolas Fonrose
 *
 */
public class QueueManager {

	//private static Logger _logger = Logger.getLogger(QueueManager.class);
	
	private static final QueueManager __instance = new QueueManager();
	
	public static final String PERCLIENT_VERIFIED_DATAIMPORT_QUEUE = "perClient-verified-DataImportQueue";
	public static final String PERCLIENT_ERROR_DATAIMPORT_QUEUE = "perClient-error-DataImportQueue";
	public static final String COMMON_VERIFIED_FIRSTMESSAGE_DATAIMPORT_QUEUE = "common-verified-FirstMessage-DataImportQueue";
	public static final String COMMON_INITIALENTRYPOINT_DATAIMPORT_QUEUE = "common-initalEntryPoint-DataImportQueue";
	
	
	
	/**
	 * Singleton implementation
	 * @return
	 */
	public static QueueManager getInstance() {
		return __instance;
	}

	/**
	 * Get a queue dedicated to a client and with the specified name.
	 * 
	 * At the SQS level, the name of the queue is made of a prefix followed by the clientId and the queueName.
	 * This means that each client can have a queue with the same name as other clients without any collision.
	 * 
	 * @param clientId
	 * @param queueName
	 * @param queueName
	 * @throws Exception 
	 */
	public SQSQueue getDedicatedQueueForClient(int clientId, String queueName) {
		String sqsQueueName = String.format("Queue-%s-%s", clientId, queueName);
		return new SQSQueue(sqsQueueName, "AwsCredentials.properties");
	}

	/**
	 * Get a queue common to all clients and with the specified name.
	 * 
	 * At the SQS level, the name of the queue is made of a prefix followed bye the queueName.
	 * This means that there can be several queues common to all clients without any collision.
	 * 
	 * @param string
	 * @return
	 */
	public SQSQueue getCommonQueueForClients(String queueName) {
		String sqsQueueName = String.format("Queue-%s", queueName);
		return new SQSQueue(sqsQueueName, "AwsCredentials.properties");
	}
	
}
