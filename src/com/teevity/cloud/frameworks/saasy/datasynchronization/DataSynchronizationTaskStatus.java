package com.teevity.cloud.frameworks.saasy.datasynchronization;

public enum DataSynchronizationTaskStatus {

	DataUploadStarted,					// The client-side has started pushing data to the Cloud
	DataReceivedAndVerified,			// All data from a client, for a specific synchronisation session has been received
	DataReceivedAndVerifiedWithErrors,	// Same as 'DataReceivedAndVerified' but some errors are reported
	
}
