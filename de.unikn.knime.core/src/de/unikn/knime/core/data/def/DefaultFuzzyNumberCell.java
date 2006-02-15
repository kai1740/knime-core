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
 * History
 *   07.07.2005 (mb): created
 */
package de.unikn.knime.core.data.def;

import de.unikn.knime.core.data.DataCell;
import de.unikn.knime.core.data.DataType;
import de.unikn.knime.core.data.FuzzyIntervalValue;
import de.unikn.knime.core.data.FuzzyNumberType;
import de.unikn.knime.core.data.FuzzyNumberValue;

/**
 * A data cell implementation holding a Fuzzy number by storing this value in 
 * three private <code>double</code> members, that is one for the core and
 * two for the min/max of the support. It also provides a fuzzy interval value.
 * 

 * @author Michael Berthold, University of Konstanz
 */
public final class DefaultFuzzyNumberCell extends DataCell implements
        FuzzyNumberValue, FuzzyIntervalValue {

    /** Minimum support value. */
    private final double m_a;

    /** Core value. */
    private final double m_b;

    /** Maximum support value. */
    private final double m_c;

    /**
     * Creates a new fuzzy number cell based on min, max support, and core.
     * 
     * @param a Minimum support value.
     * @param b Core value.
     * @param c Maximum support value.
     * @throws IllegalArgumentException If not <code>a <= b <= c</code>.
     */
    public DefaultFuzzyNumberCell(final double a, final double b, 
            final double c) {
        if (!(a <= b && b <= c)) {
            throw new IllegalArgumentException("Illegal FuzzyInterval: <" + a
                    + "," + b + "," + c + "> these numbers"
                    + " must be ascending from left to right!");
        }
        m_a = a;
        m_b = b;
        m_c = c;
    }

    /**
     * @see DataCell#getType()
     */
    public DataType getType() {
        return FuzzyNumberType.FUZZY_NUMBER_TYPE;
    }

    /**
     * @return Minimum support value.
     */
    public double getMinSupport() {
        return m_a;
    }

    /**
     * @return Core value.
     */
    public double getCore() {
        return m_b;
    }

    /**
     * @return Maximum support value.
     */
    public double getMaxSupport() {
        return m_c;
    }

    //
    // for compatibility with FuzzyIntervalValue
    //

    /**
     * @return <code>#getCore()</code>
     */
    public double getMinCore() {
        return getCore();
    }

    /**
     * @return <code>#getCore()</code>
     */
    public double getMaxCore() {
        return getCore();
    }

    /**
     * @return The center of gravity of this trapezoid membership function which
     *         are the weighted (by the area) gravities of each of the three
     *         areas (left triangle, core rectangle, right triangle) whereby the
     *         triangles' gravity point is 2/3 and 1/3 resp. is computed by the
     *         product of the gravity point and area for each interval. This
     *         value is divided by the overall membership function volume.
     */
    public double getCenterOfGravity() {
        // left support
        double a1 = (getCore() - getMinSupport()) / 2.0;
        double s1 = getMinSupport() + (getCore() - getMinSupport()) * 2.0 / 3.0;

        // right support
        double a3 = (getMaxSupport() - getCore()) / 2.0;
        double s3 = getCore() + (getMaxSupport() - getCore()) / 3.0;
        if (a1 + a3 == 0.0) {
            assert (s1 == s3);
            return s1;
        }
        return (a1 * s1 + a3 * s3) / (a1 + a3);
    }

    /**
     * @see de.unikn.knime.core.data.DataCell
     *      #equalsDataCell(de.unikn.knime.core.data.DataCell)
     */
    protected boolean equalsDataCell(final DataCell dc) {
        DefaultFuzzyNumberCell fc = (DefaultFuzzyNumberCell)dc;
        return (fc.m_a == m_a) && (fc.m_b == m_b) && (fc.m_c == m_c);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        long bits = Double.doubleToLongBits(getCenterOfGravity());
        return (int)(bits ^ (bits >>> 32));
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "<" + m_a + "," + m_b + "," + m_c + ">";
    }

}
