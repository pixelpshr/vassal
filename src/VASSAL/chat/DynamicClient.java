/*
 * $Id$
 *
 * Copyright (c) 2000-2013 by Rodney Kinney, Brent Easton
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License (LGPL) as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, copies are available
 * at http://www.opensource.org.
 */
package VASSAL.chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import VASSAL.chat.jabber.JabberClientFactory;
import VASSAL.i18n.Resources;
import VASSAL.tools.ConfigFileReader;
import VASSAL.tools.ErrorDialog;
import VASSAL.tools.ThrowableUtils;

/**
 * Determines server implementation at run-time by downloading properties from the server 
 * site (as specified in the config file). Refreshes every time the user attempts to connect.
 *
 * @author rkinney
 *
 */
public class DynamicClient extends HybridClient {

  private static final Logger logger =
		  LoggerFactory.getLogger(DynamicClient.class);

  private static String legacyURL = ""; //$NON-NLS-1$
  private static String jabberURL = ""; //$NON-NLS-1$

  private boolean connecting;
  private Properties overrides;

  public DynamicClient() {
	
	// read the config file to get the server URL
	ConfigFileReader config = new ConfigFileReader();
	
	String serverURL = ""; //$NON-NLS-1$
	serverURL = config.getServerURL() + "/util"; //$NON-NLS-1$
	
	  setLegacyURL(serverURL + "/getServerImpl");
	  setJabberURL(serverURL + "/getJabberServerImpl");
  }

  public DynamicClient(String serverConfigURL) {
	logger.info("DynamicClient(): serverConfigURL = {}", serverConfigURL); // DEBUG-MITRE
	  
	String serverURL = ""; //$NON-NLS-1$
	serverURL = serverConfigURL + "/util"; //$NON-NLS-1$
	
	  setLegacyURL(serverURL + "/getServerImpl"); //$NON-NLS-1$
	  setJabberURL(serverURL + "/getJabberServerImpl"); //$NON-NLS-1$
  }

  protected ChatServerConnection buildDelegate() throws IOException {
    final Properties p = ServerAddressBook.getInstance().getCurrentServerProperties();
    // DEBUG-MITRE
    StringWriter writer = new StringWriter();
    p.list(new PrintWriter(writer));
    System.out.println("buildDelegate(): \n"+ writer.getBuffer().toString());
    // DEBUG-MITRE
    return ChatServerFactory.build(p);
  }

/*
  private Properties getServerConfig() throws IOException {
    HttpRequestWrapper r = new HttpRequestWrapper(serverConfigURL);
    Properties p = new Properties();
    p.put("module", GameModule.getGameModule() == null ? "Test" : GameModule.getGameModule().getGameName()); //$NON-NLS-1$ //$NON-NLS-2$
    p.put("vassalVersion", VASSAL.Info.getVersion()); //$NON-NLS-1$
    List<String> l = r.doGet(p);
    if (l.isEmpty()) {
      throw new IOException(Resources.getString("Server.empty_response")); //$NON-NLS-1$
    }
    p = new Properties();
    final StringBuilder buff = new StringBuilder();
    for (String s : l) {
      buff.append(s).append('\n');
    }
    p.load(new ByteArrayInputStream(buff.toString().getBytes()));
    if (overrides != null) {
      p.putAll(overrides);
    }
    return p;
  }
*/

  public void setConnected(final boolean connect) {
    if (connect && !isConnected()) {
      if (!connecting) {
        connecting = true;
        new SwingWorker<ChatServerConnection, Void>() {
          @Override
          protected ChatServerConnection doInBackground() throws Exception {
            return buildDelegate();
          }

          @Override
          protected void done() {
            try {
              setDelegate(get());
              DynamicClient.super.setConnected(connect);
            }
            catch (InterruptedException e) {
            }
            catch (ExecutionException ex) {
              Throwable e = ex.getCause();
              fireStatus(Resources.getString("Server.bad_address3")); //$NON-NLS-1$
              ErrorDialog.showDetails(e, ThrowableUtils.getStackTrace(e), "Error.network_communication_error"); //$NON-NLS-1$
              e.printStackTrace();
            }
            connecting = false;
          }
        }.execute();
      }
    }
    else {
      super.setConnected(connect);
      if (!isConnected()) {
        try {
          setDelegate(buildDelegate());
        }
        catch (IOException ex) {
          ;
        }
      }
    }
  }

  public void setOverrides(Properties override) {
    this.overrides = override;
    if (JabberClientFactory.JABBER_SERVER_TYPE.equals(overrides.getProperty(JabberClientFactory.TYPE_KEY))) {
    }
  }

	public static String getLegacyURL() {
		return legacyURL;
	}
	
	public void setLegacyURL(String url) {
		legacyURL = url;
	}

	public static String getJabberURL() {
		return jabberURL;
	}

	public void setJabberURL(String jabberURL) {
		this.jabberURL = jabberURL;
	}
}
