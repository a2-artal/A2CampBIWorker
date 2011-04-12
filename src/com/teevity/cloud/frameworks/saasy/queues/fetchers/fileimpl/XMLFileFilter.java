package com.teevity.cloud.frameworks.saasy.queues.fetchers.fileimpl;


import java.io.*;

/**
 * A class that implements the Java FileFilter interface.
 * @author Julien Lavergne, Integration : Mathieu Passenaud, Nicolas Fonrose
 */
public class XMLFileFilter implements FileFilter
{
  private final String[] okFileExtensions = new String[] {"xml"};

  public boolean accept(File file)
  {
    for (String extension : okFileExtensions)
    {
      if (file.getName().toLowerCase().endsWith(extension))
      {
        return true;
      }
    }
    return false;
  }
}
