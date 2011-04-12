package com.teevity.cloud.frameworks.saasy.queues.fetchers;

import com.teevity.cloud.frameworks.awsy.queues.SQSQueue;

public abstract class AbstractMessageFetcher implements IMessageFetcher {

	protected SQSQueue _queueToBeFetched;

	/**
	 * Constructor
	 */
	public AbstractMessageFetcher(SQSQueue queueToBeFetched) {
		_queueToBeFetched = queueToBeFetched;
	}

}