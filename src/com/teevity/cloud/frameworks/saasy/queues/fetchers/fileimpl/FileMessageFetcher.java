package com.teevity.cloud.frameworks.saasy.queues.fetchers.fileimpl;

import java.util.List;
import org.apache.log4j.Logger;

import com.teevity.cloud.frameworks.saasy.queues.fetchers.IMessageFetcher;
import com.teevity.cloud.frameworks.saasy.queues.message.SDataMessage;
import com.teevity.cloud.frameworks.saasy.queues.message.SDataMessageFactory;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

/**
 * 
 * @author Mathieu Passenaud, Nicolas Fonrose
 *
 */
public class FileMessageFetcher implements IMessageFetcher {

    private static Logger _log = Logger.getLogger(FileMessageFetcher.class);
    private String _taskId;
    private int _clientId;
    private File[] xmlFilesList;

    /**
     * Constructor
     * @param id
     * @param clientId
     */
    public FileMessageFetcher(String taskId, int clientId) {
        _taskId = taskId;
        _clientId = clientId;
        _log.info(String.format("Created FileMessageFetcher for clientId[%d], taskId[%s]", _clientId, _taskId));
    }

    /**
     * Indicates if there are more messages to fetch for the "client task" this MessageFetcher
     * has been created for.
     */
    @Override
    public boolean hasMoreMessages() {
        // TODO Auto-generated method stub
        return ((xmlFilesList == null) || (xmlFilesList.length>0))?true:false;
    }

    /**
     * Return a list of pending messages to be fetched, up to a maximum of nbMax messages.
     * @param nbMax
     */
    @Override
    public List<SDataMessage> fetchMessages(int nbMax) throws Exception {
        try {

            List<SDataMessage> sDataMessagesList = new ArrayList<SDataMessage>();

            /**
             * How to access to SDATA msgs (XML files)
             */
            try {

                File msgsDirectory = new File("messages");
                FileFilter f = new XMLFileFilter();
                xmlFilesList = msgsDirectory.listFiles(f);

                for (File xmlfile : xmlFilesList) {
                    sDataMessagesList.add(SDataMessageFactory.createSDataMessage(xmlfile));
                    xmlfile.delete();
                }   
                return sDataMessagesList;

            } catch (Exception e) {

                System.out.println(e.getMessage());

            }
            return null;

        } catch (Exception ex) {
            _log.error(String.format("Fetching messages failed for clientId[%d], taskId[%s]", _clientId, _taskId), ex);
            throw ex;
        }
    }
}
