package com.teevity.cloud.frameworks.saasy.queues.fetchers.s3impl;

import java.util.List;
import java.util.ArrayList;

import com.teevity.cloud.frameworks.awsy.queues.SQSQueue;
import com.teevity.cloud.frameworks.saasy.queues.fetchers.AbstractMessageFetcher;
import com.teevity.cloud.frameworks.saasy.queues.message.SDataMessage;

/**
 * A 'not implemented yet' message fetcher that uses Amazon S3 as a source of messages.
 * These messages would be stored as files inside directories in an S3 bucket.
 * 
 * In order to implement it, we need to specify how the messages would be stored, where, ...
 * 
 * @author nicolas
 *
 */
public class S3MessageFetcher extends AbstractMessageFetcher {

	/**
	 * 
	 * @param syncTaskId
	 * @param clientId
	 */
	public S3MessageFetcher(SQSQueue queueToBeFetched) {
		super(queueToBeFetched);
	}

	@Override
	public boolean hasMoreMessages() throws Exception {
		return false;
	}

	@Override
	public List<SDataMessage> fetchMessages(int nbMax) throws Exception {
		return new ArrayList<SDataMessage>();
	}

}
