package com.teevity.cloud.frameworks.saasy.queues.fetchers;

import java.util.List;
import com.teevity.cloud.frameworks.saasy.queues.message.SDataMessage;


public interface IMessageFetcher {

	/**
	 * Indicates if there are more messages to fetch for the "client task" this MessageFetcher
	 * has been created for.
	 * @return
	 */
	boolean hasMoreMessages() throws Exception;
	
	/**
	 * Return a list of pending messages to be fetched, up to a maximum of nbMax messages.
	 * @param nbMax
	 * @return
	 * @throws Exception 
	 */
	List<SDataMessage> fetchMessages(int nbMax) throws Exception;
	
}
