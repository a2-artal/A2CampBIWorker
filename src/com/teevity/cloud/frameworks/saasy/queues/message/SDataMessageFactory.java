package com.teevity.cloud.frameworks.saasy.queues.message;


import java.io.File;
import java.io.StringReader;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import com.amazonaws.services.sqs.model.Message;

/**
 *
 * @author Julien Lavergne, Integration : Mathieu Passenaud, Nicolas Fonrose
 */
public class SDataMessageFactory {

	private static Logger _logger = Logger.getLogger(SDataMessageFactory.class);

    
    /**
     * Creates an SDataMessage from an XML file
     * @param rawMessage
     * @return
     */
    public static SDataMessage createSDataMessage(File file) {
    	try {
    		// Parse the message from a String
    		SAXBuilder builder = new SAXBuilder();
    		Document document = builder.build(file);
    		// Creates an SDataMessage from it
    		return createSDataMessage(document);
    	} catch (Exception e) {
    		_logger.error("SDataMessage creation from RawMessage in file failed", e);
    		throw new RuntimeException("SDataMessage creation from RawMessage failed - " + e.getMessage(), e);
    	}
    }
    
    /**
     * Creates an SDataMessage from a text message 
     * @param messageText
     * @return
     */
    public static SDataMessage createSDataMessage(String messageText) {
    	try {
    		// Parse the message from a String
    		SAXBuilder builder = new SAXBuilder();
    		Document document = builder.build(new StringReader(messageText));
    		// Creates an SDataMessage from it
    		return createSDataMessage(document);
    	} catch (Exception e) {
    		_logger.error("SDataMessage creation from RawMessage String failed", e);
    		throw new RuntimeException("SDataMessage creation from RawMessage failed - " + e.getMessage(), e);
    	}
    }

    /**
     * Creates an SDataMessage from an AWS API Message Object
     * @param awsRawMessage
     * @return
     */
    public static SDataMessage createSDataMessage(Message awsRawMessage) {
        try {
        	// Extract the text body from the raw message and store the original 'AWS raw message'
        	String rawMsgBody = awsRawMessage.getBody();
        	// Parse the message from a String
        	SAXBuilder builder = new SAXBuilder();
        	Document document = builder.build(new StringReader(rawMsgBody));
        	// Creates an SDataMessage from it
        	return createSDataMessage(document, awsRawMessage);
        } catch (Exception e) {
            _logger.error("SDataMessage creation from RawMessage String failed", e);
            throw new RuntimeException("SDataMessage creation from RawMessage failed - " + e.getMessage(), e);
        }
    }
    
    /**
     * Creates an SDataMessage from a parsed XML String
     * @param rawMessage
     * @return
     */
    private static SDataMessage createSDataMessage(Document document) {
    	return createSDataMessage(document, null);
    }
    
    
    /**
     * Creates an SDataMessage from an AWS Message (called "Raw" message)
     * @param rawMessage
     * @return
     */
    private static SDataMessage createSDataMessage(Document document, Message rawMessage) {
        SDataMessage sDataMessage = null;

    	// Find the root element of the Atom entry
        Element atomEntryRootElement = document.getRootElement();
        
        // Find the payload element
        Namespace ns = Namespace.getNamespace("sdata", "http://schemas.sage.com/sdata/2008/1");
        Element payloadElement = atomEntryRootElement.getChild("payload", ns);
        
        // Find the root element of the payload content (which identifies the table name)
        Element payloadContentElement = (Element) (payloadElement.getChildren().get(0));

        // Retrieve the id SDataMessage contained in XML file
        String idMessage = payloadContentElement.getAttributeValue("uuid", ns);

         // Create an SDataMessage object to wrap the content
         sDataMessage = new SDataMessage(idMessage, rawMessage, atomEntryRootElement, payloadElement, payloadContentElement);

         return sDataMessage;

    }

}
