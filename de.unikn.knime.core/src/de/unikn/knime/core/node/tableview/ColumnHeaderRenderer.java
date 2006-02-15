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
package de.unikn.knime.core.node.tableview;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import de.unikn.knime.core.data.DataColumnSpec;
import de.unikn.knime.core.data.DataType;

/**
 * Renderer to be used to display the column header of a table. It will show
 * an icon on the left and the name of the column on the right. The icon is
 * given by the type's <code>getIcon()</code> method
 * 
 * @see de.unikn.knime.core.data.DataType#getIcon() 
 * @author Bernd Wiswedel, University of Konstanz
 */
public class ColumnHeaderRenderer extends DefaultTableCellRenderer {
    
    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(
     *      JTable, Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(final JTable table, 
            final Object value, final boolean isSelected, 
            final boolean hasFocus, final int row, final int column) {
        // set look and feel of a header
        if (table != null) {
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                setForeground(header.getForeground());
                setBackground(header.getBackground());
                setFont(header.getFont());
            }
        }
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        Icon icon = null;
        Object newValue = value;
        if (value instanceof DataColumnSpec) {
            DataType columnType = ((DataColumnSpec)value).getType();
            newValue =  ((DataColumnSpec)value).getName();
            icon = columnType.getIcon();
        }
        setIcon(icon);
        setValue(newValue);
        return this;
    }

}
