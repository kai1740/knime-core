/*
 * @(#)$RCSfile$ 
 * $Revision$ $Date$ $Author$
 * --------------------------------------------------------------------- *
 *   This source code, its documentation and all appendant files         *
 *   are protected by copyright law. All rights reserved.                *
 *                                                                       *
 *   Copyright, 2003 - 2006                                              *
 *   Universitaet Konstanz, Germany.                                     *
 *   Lehrstuhl fuer Angewandte Informatik                                *
 *   Prof. Dr. Michael R. Berthold                                       *
 *                                                                       *
 *   You may not modify, publish, transmit, transfer or sell, reproduce, *
 *   create derivative works from, distribute, perform, display, or in   *
 *   any way exploit any of the content, in whole or in part, except as  *
 *   otherwise expressly permitted in writing by the copyright owner.    *
 * --------------------------------------------------------------------- *
 */
package de.unikn.knime.core.node;

import de.unikn.knime.core.data.DataTable;
import de.unikn.knime.core.data.DataTableSpec;
import de.unikn.knime.core.node.property.hilite.HiLiteHandler;

/**
 * Abstract node port implementation which keeps a unique id and a port name.
 * The inner classes can be used to distinguish between <code>DataPort</code>
 * and <code>PredictorParamsPort</code> objects.
 * 
 * @see NodeInPort
 * @see NodeOutPort
 * 
 * @author Thomas Gabriel, University of Konstanz
 */
public abstract class NodePort {

    /** This ports unique ID assigned from the underlying node. */
    private final int m_portID;

    /** The port name which can be used for displaying purposes. */
    private String m_portName;

    /**
     * Creates a new node port with a unique ID assigned from the underlying
     * node. The default port name is "Port [portID]" and can be changed via
     * <code>#setPortName(String)</code>.
     * 
     * @param portID The port's unique id, greater or equal zero.
     * 
     * @see #setPortName(String)
     */
    NodePort(final int portID) {
        assert (portID >= 0);
        m_portID = portID;
        setPortName(null);
    }

    /**
     * @return The port name.
     */
    public final String getPortName() {
        return m_portName;
    }

    /**
     * Sets a new name for this port.
     * 
     * @param portName The new name for this port.
     */
    final void setPortName(final String portName) {
        if (portName == null || portName.trim().length() == 0) {
            m_portName = "Port [" + m_portID + "]";
        } else {
            m_portName = portName.trim();
        }
    }

    /**
     * @return This port's id.
     */
    public final int getPortID() {
        return m_portID;
    }

    /**
     * Returns <code>true</code> if this port has a connection to another
     * port.
     * 
     * @return <code>true</code> If a connection exists otherwise
     *         <code>false</code>.
     */
    public abstract boolean isConnected();

    /**
     * Interface to identify <code>DataPort</code> objects wich can return
     * <code>DataTable</code>, <code>DataTableSpec</code>,
     * <code>HiLiteHandler</code> objects.
     */
    interface DataPort {
        /**
         * @return The node port's <code>DataTable</code>.
         */
        DataTable getDataTable();

        /**
         * @return The node port's <code>DataTableSpec</code>.
         */
        DataTableSpec getDataTableSpec();

        /**
         * @return The node port's <code>HiLiteHandler</code>.
         */
        HiLiteHandler getHiLiteHandler();
    }

    /**
     * Interface to identify <code>PredcitorParamsPort</code> objects which
     * returns <code>PredictorParams</code> objects.
     */
    interface PredictorParamsPort {
        /**
         * @return The node port's <code>PredictorParams</code> object.
         */
        PredictorParams getPredictorParams();
    }

} // NodePort
