/*
 * Copyright (c) 2007 by Joel Uckelman
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License (LGPL) as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, copies are available 
 * at http://www.opensource.org.
 */


package VASSAL.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * General I/O stream manipulation utilities. This class provides static
 * utility methods to reduce boilerplate I/O code.
 *
 * @author Joel Uckelman
 * @since 3.1.0
 */ 
public class IOUtils {
  // Portions based on org.apache.commons.io.IOUtils.

  /** The default size for input buffers. */
  public static final int BUFFER_SIZE = 4096;

  /**
   * Copies bytes from an <code>InputStream</code> to an
   * <code>OutputStream</code>. This method buffers input
   * internally, so the input stream should not be a
   * <code>BufferedInputStream</code>.
   *
   * @param in the source
   * @param out the destination
   * @throws IOException if one occurs while reading or writing.
   */
  public static void copy(InputStream in, OutputStream out)
      throws IOException {
    final byte[] buffer = new byte[BUFFER_SIZE];
    int n = 0;
    while ((n = in.read(buffer)) > 0) out.write(buffer, 0, n);
  }

  /**
   * Copies chars from a <code>Reader</code> to a <code>Writer</code>.
   * This method buffers input internally, so the <code>Reader</code>
   * should not be a <code>BufferedReader</code>.
   *
   * @param in the source 
   * @param out the destination
   * @throws IOException if one occurs while reading or writing.
   */
  public static void copy(Reader in, Writer out) throws IOException {
    final char[] buffer = new char[BUFFER_SIZE];
    int n = 0;
    while ((n = in.read(buffer)) > 0) out.write(buffer, 0, n);
  }
  
  /**
   * Copies bytes from an <code>InputStream</code> to chars for a
   * <code>Writer</code>, using the default character encoding.
   * This method buffers input internally, so the input stream should
   * not be a <code>BufferedInputStream</code>.
   *
   * @param in the source
   * @param out the destination
   * @throws IOException if one occurs while reading or writing.
   */
  public static void copy(InputStream in, Writer out) throws IOException {
    copy(new InputStreamReader(in), out);
  }

  /**
   * Copies bytes from an <code>InputStream</code> to chars for a
   * <code>Writer</code>, using the specified character encoding.
   * This method buffers input internally, so the input stream should
   * not be a <code>BufferedInputStream</code>.
   *
   * @param in the source
   * @param out the destination
   * @param encoding the character encoding; <code>null</code> for default
   * @throws IOException if one occurs while reading or writing.
   */
  public static void copy(InputStream in, Writer out, String encoding)
      throws IOException {
    if (encoding == null) {
      copy(in, out);
    }
    else {
      copy(new InputStreamReader(in, encoding), out);
    }
  }

  /**
   * Copies chars from a <code>Reader</code> to bytes for an
   * <code>OutputStream</code>, using the default character encoding.
   * This method buffers input internally, so the input stream should
   * not be a <code>BufferedReader</code>.
   *
   * @param in the source
   * @param out the destination
   * @throws IOException if one occurs while reading or writing.
   */
  public static void copy(Reader in, OutputStream out)
      throws IOException {
    final OutputStreamWriter osw = new OutputStreamWriter(out);
    copy(in, osw);
    osw.flush();
  }

 /**
   * Copies chars from a <code>Reader</code> to bytes for an
   * <code>OutputStream</code>, using the specified character encoding.
   * This method buffers input internally, so the input stream should
   * not be a <code>BufferedReader</code>.
   *
   * @param in the source
   * @param out the destination
   * @param encoding the character encoding; <code>null</code> for default
   * @throws IOException if one occurs while reading or writing.
   */
  public static void copy(Reader in, OutputStream out, String encoding)
      throws IOException {
    if (encoding == null) {
      copy(in, out);
    }
    else {
      final OutputStreamWriter osw = new OutputStreamWriter(out, encoding);
      copy(in, osw);
      osw.flush();
    }
  }

  /**
   * Copies a <code>String</code> with the specified encoding to
   * an <code>InputStream</code>.
   *
   * @param input the input <code>String</code>
   * @param encoding the character encoding; <code>null</code> for default
   * @return an input stream
   */
  public static InputStream toInputStream(String input, String encoding)
      throws IOException {
    return new ByteArrayInputStream(
      encoding != null ? input.getBytes(encoding) : input.getBytes());
  }

  /**
   * Copies bytes from an <code>InputStream</code> to a <code>String</code>
   * using the specified character encoding. This method buffers input
   * internally, so the input stream should not be a
   * <code>BufferedInputStream</code>.
   *
   * @param in the source
   * @param encoding the character encoding; <code>null</code> for default
   * @return the source as a string
   * @throws IOException if one occurs while reading.
   */
  public static String toString(InputStream in, String encoding)
      throws IOException {
    final StringWriter out = new StringWriter();
    copy(in, out, encoding);
    return out.toString();
  }

  /**
   * Copies chars from a <code>Reader</code> to a <code>String</code>
   * using the specified character encoding. This method buffers input
   * internally, so the reader should not be a
   * <code>BufferedReader</code>.
   *
   * @param in the source
   * @param encoding the character encoding; <code>null</code> for default
   * @return the source as a string
   * @throws IOException if one occurs while reading.
   */
  public static String toString(Reader in) throws IOException {
    final StringWriter out = new StringWriter();
    copy(in, out);
    return out.toString();
  }

  /**
   * Copies bytes from an <code>InputStream</code> to a <code>byte[]</code>.
   * This method buffers input internally, so the input stream should not be
   * a <code>BufferedInputStream</code>.
   *
   * @param in the source
   * @return the source as a <code>byte[]</code>
   * @throws IOException if one occurs while reading.
   */
  public static byte[] toByteArray(InputStream in) throws IOException {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    copy(in, out);
    return out.toByteArray();
  }

  /**
   * Copies bytes from an <code>InputStream</code> to a <code>byte[]</code>.
   *
   * @param in the source
   * @return the source as a <code>byte[]</code> 
   * @throws IOException if one occurs while reading.
   */
  public static byte[] getBytes(InputStream in) throws IOException {
    // Note: This is here because it is 1/3 faster than toByteArray.
    // The tradeoff is hugely more memory use.
    byte buffer[] = null;
    final BufferedInputStream bufIn = new BufferedInputStream(in);
    try {
      final byte abyte0[] = new byte[bufIn.available()];

      int nCurBytes = 0;
      while ((nCurBytes = bufIn.read(abyte0, 0, abyte0.length)) != -1) {
        if (buffer == null) {
          buffer = new byte[nCurBytes];
          System.arraycopy(abyte0, 0, buffer, 0, nCurBytes);
        }
        else {
          final byte oldbuf[] = buffer;
  
          buffer = new byte[oldbuf.length + nCurBytes];
  
          System.arraycopy(oldbuf, 0, buffer, 0, oldbuf.length);
          System.arraycopy(abyte0, 0, buffer, oldbuf.length, nCurBytes);
        }
      }
    }
    finally {
      try {
        bufIn.close();
      }
      catch (IOException e) {
        ErrorLog.warn(e);
      }
    }

    return buffer != null ? buffer : new byte[0];
  }
}