package com.teevity.cloud.frameworks.awsy.queues;

import java.io.IOException;
import java.util.List;
import org.apache.log4j.*;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;


/**
 * This class wraps an SQS queue.
 * Use the constructor to build a wrapper and to initialize the connection to the queue.
 * 
 * @author Mathieu Passenaud, Nicolas Fonrose
 * @version 0.4
 * 
 *
 */
public class SQSQueue {

	private Logger _logger = Logger.getLogger(SQSQueue.class);

	private String _queueName;
	private String _parametersFile;
	private AmazonSQS _sqsConnection=null;
	private String queueURL = null;
	
	/**
	 * Create the SQS Queue wrapper and initialize the connection to AWS
	 * If the underlying queue already exists, it connects to it. If it doesn't, the queue
	 * gets created.
	 * @param queueName the name of the queue to use or to create
	 * @param parametersFile the file containing keys to connect to amazon account
	 */
	public SQSQueue(String queueName, String parametersFile) {
		try {
			this._queueName = queueName;
			this._parametersFile = parametersFile; 
			_logger.info("Parameters file"+this._parametersFile);
			this.initialiseConnection();
		} catch (Exception e) {
			_logger.warn(String.format("SQSQueue [%s] creation failed", queueName), e);
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * Initialize the connection to the SQS queue
	 */
	private void initialiseConnection() throws Exception {
		try {
			// Connect to amazon account and set the SQS endpoint to the EU-WEST-1 region
			_sqsConnection = new AmazonSQSClient(new PropertiesCredentials( SQSQueue.class.getClassLoader().getResourceAsStream(this._parametersFile)));
			_sqsConnection.setEndpoint("sqs.eu-west-1.amazonaws.com");
			_logger.info(String.format("Connection to the AWS account '%s' initialized", _queueName));

			// Get the reference to the queue or create it if it does not exist
			_logger.info(String.format("Creating or retrieving an SQS queue '%s'", _queueName));
			CreateQueueRequest createQueueRequest = new CreateQueueRequest( this._queueName );
			this.queueURL = _sqsConnection.createQueue( createQueueRequest ).getQueueUrl();
			_logger.info(String.format("SQS queue successfully retreived or created [%s=%s]", _queueName, queueURL));
			
		} catch (IOException e) {
			String message = String.format("Unable to connect to the Amazon account with these credentials [%s]", _parametersFile); 
			_logger.error(message, e);
			throw new Exception(message);
		} catch (AmazonServiceException ase) {
			String message = String.format("Unable to create or retrieve queue '%s'", _queueName);
			_logger.error(message, ase);
			throw new Exception(message);
		}

	}

	/**
	 * Push data in the queue
	 * 
	 * @param data
	 */
	public void pushData(String data) throws Exception {
		// Verify if data is not too long
		if (data.length() > 8 * 1024) {
			throw new Exception(String.format("SQS message max size exceeded [size=%d]", data.length()));
		}
		
		// Push it into the queue
		_sqsConnection.sendMessage(new SendMessageRequest( this.queueURL, data ));
		
	}
	
	/**
	 * Get first message in the queue
	 * @return one message
	 */
	public Message getFirstMessage() {
		List<Message> messages = getMessages(1);
		return messages.get(0);
	}
	
	/**
	 * 
	 * Get all messages in the queue
	 * @return all messages
	 */
	public List<Message> getMessages(int nbMaxMessages) {
		ReceiveMessageRequest receiveRequest = new ReceiveMessageRequest(this.queueURL);
		receiveRequest.setMaxNumberOfMessages(nbMaxMessages);
		List<Message> messages = _sqsConnection.receiveMessage(receiveRequest).getMessages();
		return messages;
	}
	
	/**
	 * 
	 * Get all messages in the queue
	 * @return all messages
	 */
	public List<Message> getMessages(int nbMaxMessages, int visibilityTimeout) {
		ReceiveMessageRequest receiveRequest = new ReceiveMessageRequest(this.queueURL);
		receiveRequest.setMaxNumberOfMessages(nbMaxMessages);
		receiveRequest.setVisibilityTimeout(visibilityTimeout);
		List<Message> messages = _sqsConnection.receiveMessage(receiveRequest).getMessages();
		return messages;
	}
	
	/**
	 * Delete the message in the queue
	 * @param message
	 */
	public void deleteMessage(Message message){
		_sqsConnection.deleteMessage(new DeleteMessageRequest(this.queueURL, message.getReceiptHandle()));
	}
	
	/**
	 * Delete the queue
	 */
	public void deleteQueue(){
		_sqsConnection.deleteQueue(new DeleteQueueRequest(this.queueURL));
		_logger.info(String.format("Queue deleted [%s=%s]", _queueName, queueURL));
	}
	
	/**
	 * 
	 * @return the name of the queue
	 */
	public String getQueueName() {
		return _queueName;
	}

	/**
	 * 
	 * @return the approximate size of the queue (the exact size cannot be retrieved by AWS)
	 */
	public int getApproximateSize() {
		String approxNbMessageAsString = _sqsConnection.getQueueAttributes(new GetQueueAttributesRequest(this.queueURL)).getAttributes().get("ApproximateNumberOfMessages");
		if (approxNbMessageAsString == null) {
			return 0;
		} else {
			return Integer.parseInt(approxNbMessageAsString);
		}
	}

	/**
	 * 
	 * @return the URL identifying this queue.
	 */
	public String getEndpointURL() {
		return this.queueURL;
	}

}
