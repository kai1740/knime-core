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
 */
package de.unikn.knime.core.node;

/**
 * Listens for progress change events.
 * 
 * @author Thomas Gabriel, Konstanz University
 */
public interface NodeProgressListener {

    /**
     * Invoked when the progress has changed.
     * @param progress The progress value between 0.0 and 1.0, or -1
     *        if no progress available.
     * @param message The progress message to be displayed.
     */
    void progressChanged(double progress, String message);
    
}
