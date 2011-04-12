package com.teevity.cloud.frameworks.saasy.queues.message;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;

import com.amazonaws.services.sqs.model.Message;


/**
 * 
 * @author Mathieu Passenaud, Nicolas Fonrose
 *
 */
public class SDataMessage {

	private static Logger _log = Logger.getLogger(SDataMessage.class);

	private String _id;
	private Element _atomEntryRootElement;
	private Element _payloadElement;
	private Element _payloadContentElement;
	private Message _awsMessage;

    /**
     * constructor used in XMLImport to initialize
     * a SDataMessage object
     * @param _id
     * @param _message
     */
    public SDataMessage(String id, Element atomEntryRootElement, Element payloadElement, Element payloadContentElement) {
    	// Store the basic elements
        _id = id;
        _atomEntryRootElement = atomEntryRootElement;
        _payloadElement = payloadElement;
		_payloadContentElement = payloadContentElement;
    };
    
    /**
     * constructor used in XMLImport to initialize
     * a SDataMessage object
     * @param _id
     * @param _message
     */
    public SDataMessage(String id, Message awsMessage, Element atomEntryRootElement, Element payloadElement, Element payloadContentElement) {
    	// Store the basic elements
    	_id = id;
    	_awsMessage = awsMessage;
    	_atomEntryRootElement = atomEntryRootElement;
    	_payloadElement = payloadElement;
    	_payloadContentElement = payloadContentElement;
    };
    
    /**
     * Return the ATOM message content as a JDOM XML Element
     * @return message (JDOM XML Element)
     */
    public Element getAtomEntryRootElement() {
    	return _atomEntryRootElement;
    }

    /**
     * Return the SDATA message content as a JDOM XML Element
     * @return message (JDOM XML Element)
     */
    public Element getPayloadRootElement() {
        return _payloadContentElement;
    }

    /**
     * 
     * @return
     */
	public int getClientId() {
        Namespace ns = Namespace.getNamespace("http://schemas.sage.com/sdbBI");
		String clientIdAsText = ((Element)_payloadContentElement.getChildren("idTenant", ns).get(0)).getText();
		return Integer.parseInt(clientIdAsText);
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isFirstPage() {
		try {
			return "true".equals(_payloadElement.getAttributeValue("firstPage"));
		} catch (Exception e) {
			_log.warn(String.format("FAILED to find 'firstPage' attribute in the payload of the SDataMessage[id=%d]. Error [%s]", _id, e.getMessage()));
			return false;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isLastPage() {
		try {
			return "true".equals(_payloadElement.getAttributeValue("lastPage"));
		} catch (Exception e) {
			_log.warn(String.format("FAILED to find 'lastPage' attribute in the payload of the SDataMessage[id=%d]. Error [%s]", _id, e.getMessage()));
			return false;
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getTaskId() {
		try {
			return _payloadElement.getAttributeValue("taskId");
		} catch (Exception e) {
			_log.error(String.format("FAILED to find 'taskId' attribute in the payload of the SDataMessage[id=%d]. Error [%s]", _id, e.getMessage()));
			return "taskId-NOTASKID";
		}
	}

	/**
	 * 
	 * @return
	 */
	public Message getAwsMessage() {
		if (_awsMessage == null) {
			throw new RuntimeException(String.format("SDataMessage[%s] not associated with an AWS Message", _id));
		}
		return _awsMessage;
	}
	
}
