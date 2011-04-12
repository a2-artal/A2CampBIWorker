package com.teevity.cloud.frameworks.saasy.queues.fetchers.sqsimpl;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.amazonaws.services.sqs.model.Message;
import com.teevity.cloud.frameworks.saasy.queues.message.SDataMessage;
import com.teevity.cloud.frameworks.saasy.queues.message.SDataMessageFactory;
import com.teevity.cloud.frameworks.awsy.queues.SQSQueue;
import com.teevity.cloud.frameworks.saasy.queues.fetchers.AbstractMessageFetcher;


/**
 * This class makes it possible to fetch messages from a queue in which a finite number
 * of messages will arrive and where the first and last message are identified (in some way,
 * for instance a marker in the message itself).
 * 
 * When no more messages are bound to be received through this queue, the hasMoreMessage method
 * return false; 
 * 
 * @author nicolas
 *
 */
public class FiniteQueueSQSMessageFetcher extends AbstractMessageFetcher {

	private static final int NBMAXMESSAGEPERFETCH = 10;
	private static final int TIMEBETWEENCHECKS = 1000;     // 1 seconde
	private static final int TIMEOUT = 1000*5*60;          // 5 minutes
	private static final int TIMEAFTERLASTMESSAGEBEFORETHEQUEUEISCONSIDEREDREALLYEMPTY = 1000*30*60; // 30 minutes

	private static Logger _log = Logger.getLogger(FiniteQueueSQSMessageFetcher.class);
	private boolean _hasReceivedLastMessage = false;
	
	/**
	 * Constructor of the SQSMessageFetcher. A new instance of this class is created everytime
	 * there is a new synchronization task to perform for a client
	 * @param syncTaskId
	 * @param clientId
	 * @throws Exception 
	 */
	public FiniteQueueSQSMessageFetcher(SQSQueue queueToFetch) {
		// Initialize the base class
		super(queueToFetch);
		
		// Get the connection to the SQS queue dedicated to that client
		try {
			_log.info(String.format("Created FiniteQueueSQSMessageFetcher for queue [%s]", _queueToBeFetched.getQueueName()));
		} catch (Exception e) {
			_log.error(String.format("Failed to created the  SQSMessageFetcher for queue [%s]", _queueToBeFetched.getQueueName()), e);
			throw new RuntimeException("SQSMessageFetcher initialization error", e);
		}
	}

	@Override
	public boolean hasMoreMessages() {
		// TODO - Trouver une solution à ce problème : In the case of an AWS queue, the size returned by the 
		//        infrastructure is approximate.
		//        This means that the only real way to know if there are more messages to fetch 
		//        is to get the emitter to specify upfront how many messages it is going to send so that
		//        we know if there are more things to fetch (and if we need to wait if the current queue size is 0)
		
		if (_queueToBeFetched.getApproximateSize() > 0) {
			return true;
		} else {
			// La file est vide, mais on n'a pas encore eu le dernier message.
			if (_hasReceivedLastMessage == false) {
				// On boucle en attendant que de nouveaux messages arrivent
				long timeWaitingElapsed = 0;
				while (timeWaitingElapsed < TIMEOUT) {
					// Wait before the checks
					try {
						Thread.sleep(TIMEBETWEENCHECKS);
					} catch (InterruptedException e) {
						_log.warn("Thread interrupted while waiting between checks to the queue", e);
					}
					timeWaitingElapsed += TIMEBETWEENCHECKS;
					// Check the queue again
					if (_queueToBeFetched.getApproximateSize() > 0) {
						return true;
					}
				}
				// Synchronization en echec sur la tâche. Dernier message non encore arrivé au moment du timeout
				_log.warn(String.format("The last message hasn't been received yet but we have reached the timeout (%d minutes)", ((int)TIMEOUT/5/60)));
				return false;
			} else {
				// On considère que si la file est vide et qu'on a reçu le dernier message, alors la tâche
				// de synchronization est finie.
				//
				// En réalité, on ne peut le savoir qu'en observant les identifiants de messages reçus pour savoir
				// s'il en manque encore (ou en connaissant à l'avance le nombre de messages qui doivent être reçus
				// et en le comparant au nombre effectivement arrivé).
				// 
				// Notre solution approximative à ce problème consiste à attendre un certain temps, lorsque la file est
				// vide et qu'on a reçu le dernier message, avant de considérer que la tâche de synchronization est finie.
				try {
					Thread.sleep(TIMEAFTERLASTMESSAGEBEFORETHEQUEUEISCONSIDEREDREALLYEMPTY);
				} catch (InterruptedException e) {
					_log.warn("Thread interrupted while waiting before we consider the queue to be really empty.", e);
				}
				if (_queueToBeFetched.getApproximateSize() > 0) {
					return true;
				} else {
					// On considère que la synchronization est finie
					_log.info(String.format("No more messages in queue [%s]", _queueToBeFetched.getQueueName()));
					return false;
				}
			}
		}
	}

	@Override
	public List<SDataMessage> fetchMessages(int nbMax) {
		// Get some messages from the queue
		List<SDataMessage> result = new ArrayList<SDataMessage>();
		List<Message> rawResult = _queueToBeFetched.getMessages(NBMAXMESSAGEPERFETCH);
		_log.info(String.format("Just fetched '%d' messages in queue [%s]", rawResult.size(), _queueToBeFetched.getQueueName()));
		
		// Parse the messages retreived from the queue and create SDataMessage from them
		for (Message awsRawMessage : rawResult) {
			result.add(SDataMessageFactory.createSDataMessage(awsRawMessage));
		}
		return result;
	}

	
	
}
