package com.teevity.cloud.frameworks.saasy.queues.fetchers.sqsimpl;

import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.amazonaws.services.sqs.model.Message;
import com.teevity.cloud.frameworks.saasy.queues.message.SDataMessage;
import com.teevity.cloud.frameworks.saasy.queues.message.SDataMessageFactory;
import com.teevity.cloud.frameworks.awsy.queues.SQSQueue;
import com.teevity.cloud.frameworks.saasy.queues.fetchers.AbstractMessageFetcher;


/**
 * This class fetch messages from an infinite SQS queue (a queue which is supposed
 * to continuously receive messages).
 * 
 * The hasMoreMessage always returns true, even if it ways a little bit before doing 
 * so in order for the reader of the queue to avoid 
 * hitting the SQS infrastructure too hard (for no reason).
 *  
 * @author nicolas
 *
 */
public class BasicSQSMessageFetcher extends AbstractMessageFetcher {

	private static Logger _log = Logger.getLogger(BasicSQSMessageFetcher.class);

	private static final int NBMAXMESSAGEPERFETCH = 10;
	private static final int TIMEBETWEENCHECKS = 1000;					// 1 seconde
	private int _timeOutBeforeHasMoreMessageReturnsFalse = 1000*5*60;	// 5 minutes

	/**
	 * Constructor of the BasicSQSMessageFetcher. A new instance of this class is created everytime
	 * there is a new synchronization task to perform for a client
	 * @param queueToBeFetched	The queue to fetch messages from
	 * @param timeOutBeforeHasMoreMessageReturnsFalse	The time hasMoreMessage tries to get messages from the queue and find it empty before it really says its empty 
	 * @throws Exception 
	 */
	public BasicSQSMessageFetcher(SQSQueue queueToBeFetched, int timeOutBeforeHasMoreMessageReturnsFalse) {
		// Initialize the base class
		super(queueToBeFetched);
		_timeOutBeforeHasMoreMessageReturnsFalse = timeOutBeforeHasMoreMessageReturnsFalse;
		
		// TODO - Ajouter un paramètre 'int timeBetweenChecks' qui permet de configurer cette valeur. Voir comment coordonner sa valeur avec celle de 'timeOutBeforeHasMoreMessageReturnsFalse' car la situation 'timeBetweenChecks > timeOutBeforeHasMoreMessageReturnsFalse' n'a pas de sens.  
		
		// Get the connection to the SQS queue dedicated to that client
		try {
			_log.info(String.format("Created BasicSQSMessageFetcher for queue[%s]", _queueToBeFetched.getQueueName()));
		} catch (Exception e) {
			_log.error(String.format("Failed to created the  BasicSQSMessageFetcher for queue[%s]", _queueToBeFetched.getQueueName()));
			throw new RuntimeException("SQSMessageFetcher initialization error", e);
		}		
	}

	/**
	 * The hasMoreMessage always returns true, even if it ways a little bit before doing 
	 * so in order for the reader of the queue to avoid hitting the SQS infrastructure 
	 * too hard (for no reason).
	 */
	@Override
	public boolean hasMoreMessages() {
		if (_queueToBeFetched.getApproximateSize() > 0) {
			return true;
		} else {
			// On boucle en attendant que de nouveaux messages arrivent
			long timeWaitingElapsed = 0;
			while (timeWaitingElapsed < _timeOutBeforeHasMoreMessageReturnsFalse) {
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
			// 
			if (_timeOutBeforeHasMoreMessageReturnsFalse > 0) {
				_log.warn("Timeout reached on this queue for the hasMoreMessages() method");
			}
			return false;
		}
	}
	

	@Override
	public List<SDataMessage> fetchMessages(int nbMax) {
		// Get some messages from the queue
		List<SDataMessage> result = new ArrayList<SDataMessage>();
		List<Message> rawResult = _queueToBeFetched.getMessages(NBMAXMESSAGEPERFETCH);
		
		// Parse the messages retreived from the queue and create SDataMessage from them
		for (Message rawMessage : rawResult) {
			result.add(SDataMessageFactory.createSDataMessage(rawMessage));
		}
		return result;
	}
	
}
