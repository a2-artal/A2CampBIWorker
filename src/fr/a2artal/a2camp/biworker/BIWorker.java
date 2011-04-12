package fr.a2artal.a2camp.biworker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import com.amazonaws.services.sqs.model.Message;
import com.teevity.cloud.frameworks.awsy.queues.SQSQueue;
import com.teevity.cloud.frameworks.awsy.simpleDB.SimpleDB;

/**
 * A Worker that computes
 * 
 * @author nicolas
 */
public class BIWorker implements Runnable {

	private static int SQS_SLEEP_DURATION_BETWEEN_READS_INSEC = 1;
	private static int NB_MESSAGE_PER_READBATCH = 1;
	private static String PATH_TO_BIDATASTORE = "/";
	
	private String _workerName = ""; 
	private SQSQueue _sqsUpdateQueue;
	private SimpleDB _simpleDB_BIResultStore_Domain;
	
	/**
	 * 
	 * @param string
	 */
	public BIWorker(String workerName) {
		_workerName = workerName;
		// Create the 'connections' to the technical resources we use in this component
		try {
			_sqsUpdateQueue = new SQSQueue("A2Camp-BI-UpdatesQueue", "AwsCredentials.properties");
			_simpleDB_BIResultStore_Domain = new SimpleDB("A2Camp-BI-ResultStore", "AwsCredentials.properties", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Main loop of the worker
	 */
	public void run() {
		while (true) {
			// Find some work to do in the queue
			List<Message> updateInfoItems = _sqsUpdateQueue.getMessages(NB_MESSAGE_PER_READBATCH);
			// If the methods does'nt return anything, we wait a little bit and fetch again
			if ((updateInfoItems == null) || (updateInfoItems.size() == 0)) {
				try {
					Thread.sleep(SQS_SLEEP_DURATION_BETWEEN_READS_INSEC*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// Perform the BI work
			String computedBIResult = simulateBIWork(updateInfoItems);
			// Store the result
			storeResult(computedBIResult);
		}
	}

	
	/**
	 * 
	 * @param computedBIResult
	 */
	private void storeResult(String computedBIResult) {
		// Create an entity that will store the value of the 'Average Nb of Tickets bought by users' at a given time
		List<String> attributes = Arrays.asList(new String[] {"avgNbTicketsBought", "time"});
		List<String> values     = Arrays.asList(new String[] {computedBIResult, System.currentTimeMillis()+""});
		// Store it in SimpleDB
		try {
			_simpleDB_BIResultStore_Domain.addItem("biResult" + System.nanoTime(), attributes, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 
	 * @param updateInfoItems
	 * @return
	 */
	private String simulateBIWork(List<Message> updateInfoItems) {
		
		double crunchedResult = Math.random() * 10;
		
		// Iterate over the list 
		for (Message item : updateInfoItems) {
			// Get the body of the message
			String dataItem = item.getBody();
			// Load the data related to that update from the 'PATH_TO_BIDATASTORE'
			StringBuffer data = new StringBuffer();
			// Crunch it
			
			// Accumulate the crunched result
			
		}
		// Return the crunched results
		return "Average number of seats booked by user = '" + crunchedResult + "' (computed by worker [" + _workerName + "])";
	}

}
