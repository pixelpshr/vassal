/*
 * $Id$
 *
 * Copyright (c) 2007 by Rodney Kinney, Brent Easton
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
package VASSAL.i18n;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;

/**
 * Utility class to allow translation of VASSAL using the Component Translation mechanism
 * 
 * @author Brent Easton
 * 
 */
public class VassalTranslation extends Translation {

  protected String[] allKeys;
  
  protected Properties baseValues = new Properties();

  public VassalTranslation() {
    setConfigureName("VASSAL");
    try {
      baseValues.load(getClass().getResourceAsStream("VASSAL.properties"));
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  protected String getI18nPrefix() {
    return "";
  }

  protected String getDescription() {
    return "VASSAL";
  }

  public void clearProperties() {
    localProperties = new Properties();
    dirty = false;
    allKeys = null;
  }

  public String getAttributeValueString(String key) {
    return baseValues.getProperty(key);
  }
  
  public String[] getAttributeDescriptions() {
    initkeys();
    return allKeys;
  }

  public String[] getAttributeNames() {
    initkeys();
    return allKeys;
  }
  
  public Class[] getAttributeTypes() {
    initkeys();
    Class[] types = new Class[allKeys.length];
    Arrays.fill(types, String.class);
    return types;
  }

  protected void initkeys() {
    if (allKeys == null) {
      allKeys = Resources.getVassalKeys().toArray(new String[0]);
    }
  }

  public void saveProperties(File file, Locale locale) throws IOException {
    OutputStream out = new FileOutputStream(file);
    localProperties.store(out, locale.getDisplayName());
    dirty = false;
  }

  protected void loadProperties(InputStream in) throws IOException {
    localProperties.load(in);
    dirty = false;
  }
}