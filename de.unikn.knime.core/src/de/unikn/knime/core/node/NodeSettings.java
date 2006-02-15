/* @(#)$RCSfile$ 
 * $Revision$ $Date$ $Author$
 * 
 * -------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 * 
 * Copyright, 2003 - 2006
 * Universitaet Konstanz, Germany.
 * Lehrstuhl fuer Angewandte Informatik
 * Prof. Dr. Michael R. Berthold
 * 
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner.
 * -------------------------------------------------------------------
 * 
 */
package de.unikn.knime.core.node;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import de.unikn.knime.core.node.config.Config;

/**
 * This class overwrites the general <code>Config</code> object and
 * specializes some method to access <code>NodeSettings</code> object. This
 * object is used within the node packages.
 * 
 * @author Thomas Gabriel, University of Konstanz
 */
public class NodeSettings extends Config {

    /**
     * Creates a new instance of this object with the given key.
     * 
     * @param key An identifier.
     */
    public NodeSettings(final String key) {
        super(key);
    }

    /**
     * @see Config#getInstance(java.lang.String)
     */
    public NodeSettings getInstance(final String key) {
        return new NodeSettings(key);
    }

    /**
     * @see Config#readFromFile(java.io.ObjectInputStream)
     */
    public static synchronized NodeSettings readFromFile(
            final ObjectInputStream ois) throws IOException {
        return (NodeSettings)Config.readFromFile(ois);
    }

    /**
     * Reads settings from a given XML input stream and writes them into the
     * given <code>NodeSettings</code> object.
     * 
     * @param settings The object to write into.
     * @param in XML input stream to read settings from.
     * @throws IOException If the stream could not be read.
     * @throws NullPointerException If one of the arguments is 
     *         <code>null</code>.
     */
    public static synchronized void loadFromXML(final NodeSettings settings,
            final InputStream in) throws IOException {
        Config.loadFromXML(settings, in);
    }

    /**
     * Reads settings from the given XML stream and returns a new
     * <code>NodeSettings</code> object.
     * 
     * @param in XML input stream to read settings from.
     * @return A new settings object.
     * @throws IOException If the stream could not be read.
     * @throws NullPointerException If one of the arguments is 
     *         <code>null</code>.
     */
    public static synchronized NodeSettings loadFromXML(final InputStream in)
            throws IOException {
        NodeSettings tmpSettings = new NodeSettings("tmp");
        return (NodeSettings)Config.loadFromXML(tmpSettings, in, true);
    }

    /**
     * @see Config#addConfig(java.lang.String)
     */
    public NodeSettings addConfig(final String key) {
        return (NodeSettings)super.addConfig(key);
    }

    /**
     * @see Config#getConfig(java.lang.String)
     */
    public NodeSettings getConfig(final String key)
            throws InvalidSettingsException {
        return (NodeSettings)super.getConfig(key);
    }

}
