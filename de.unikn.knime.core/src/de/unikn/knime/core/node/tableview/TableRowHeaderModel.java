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
package de.unikn.knime.core.node.tableview;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import de.unikn.knime.core.data.DataCell;
import de.unikn.knime.core.data.RowKey;
import de.unikn.knime.core.data.def.DefaultStringCell;
import de.unikn.knime.core.node.property.ColorAttr;

/** 
 * Model for a Row Header view in a table view that displays a 
 * <code>DataTable</code>. This model has exactly one column that contains the
 * keys (type <code>DataCell</code>) of the <code>DataRow</code>s in the
 * underlying <code>DataTable</code>. The view to this model is a 
 * <code>TableRowHeaderView</code> which can be located, for instance, in a 
 * scroll pane's row header view. 
 * 
 * <p>An instance of this class always corresponds to an instance of 
 * <code>TableContentModel</code>.
 * @see de.unikn.knime.core.node.tableview.TableContentModel
 *  
 * @author Bernd Wiswedel, University of Konstanz
 */
public class TableRowHeaderModel extends AbstractTableModel {
    
    /* This model is detached from a TableContentModel (where it nevertheless 
     * has a pointer to) since it has a different column count. It was not an 
     * option to implement a smart TableRowHeaderView based on a 
     * TableContentModel because all get functions in view and model would 
     * differ then.
     */
    
    /** Reference to underlying TableContentModel, never null. */
    private TableContentInterface m_contentInterface;
    
    /** Listener on m_contentInterface. */
    private TableModelListener m_contentListener; 

    /**
     * In some, very rare cases we need to set the column name - which,
     * by default is just "Key".
     */
    private String m_columnName = "Key";

    
    /** 
     * Instantiates a new <code>TableRowHeaderModel</code> based on a 
     * <code>TableContentModel</code>. This constructor is used in 
     * the method <code>getNewRowHeaderModel()</code> in class 
     * <code>TableRowHeaderModel</code> and shouldn't be called from any place 
     * else.
     * @param content the model for the content to this row header
     * @throws NullPointerException if argument is <code>null</code> 
     */
    TableRowHeaderModel(final TableModel content) {
        if (content == null) {
            throw new NullPointerException("Content model must not be null!");
        }
        m_contentListener = new TableModelListener() {
            /** Catches events from content model, and passes it to the 
             * listeners (event's source is changed, however). UPDATE events
             * are ignored
             */
            public void tableChanged(final TableModelEvent e) {
                if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
                    return;
                }
                final int col = e.getColumn();
                if (col != TableModelEvent.ALL_COLUMNS) {
                    return; // don't care about those events.
                }
                // rows have been inserted (most likely) or deleted
                final int firstRow = e.getFirstRow();
                final int lastRow = e.getLastRow();
                final int type = e.getType();
                final TableModelEvent newEvent = new TableModelEvent(
                    TableRowHeaderModel.this, firstRow, lastRow, col, type);
                fireTableChanged(newEvent);
            } // tableChanged(TableModelEvent)
            
        };
        setTableContent(content);
    } // TableRowHeaderModel(TableContentModel)

    /** 
     * Returns 1. A row header model only has one column ... the key.
     * @return 1.
     */
    public int getColumnCount() {
        return 1;
    } // getColumnCount()

    /** 
     * Returns row count as in <code>TableContentModel</code>.
     * @return <code>getContentModel().getRowCount()</code>
     * @see TableContentModel#getRowCount()
     */
    public int getRowCount() {
        return m_contentInterface.getRowCount();
    } // getRowCount()

    /** 
     * Returns the key of the row with index <code>rowIndex</code>.
     * @param rowIndex The row of interest.
     * @param columnIndex Must be 0.
     * @return The key of the <code>DataRow</code> (type <code>DataCell</code>).
     * @throws IndexOutOfBoundsException If <code>columnIndex</code> is not 0 or
     *         <code>rowIndex</code> violates its range.
     */
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        boundColumn(columnIndex);
        // will throw IndexOutOfBoundsException if rowIndex is invalid
        return m_contentInterface.getRowKey(rowIndex);
    } // getValueAt(int, int)

    /** 
     * Return <code>DataCell.class</code> since the key of a 
     * <code>DataRow</code> is a <code>DataCell</code>.
     * @param columnIndex must be 0
     * @return <code>DataCell.class</code>
     */
    public Class<DataCell> getColumnClass(final int columnIndex) {
        boundColumn(columnIndex);
        return DataCell.class;
    } // getColumnClass(int)

    /** 
     * Returns "Key" as default row header name.
     * @param column Must be 0.
     * @return "Key"
     * @throws IndexOutOfBoundsException if column is not 0
     */
    public String getColumnName(final int column) {
        boundColumn(column);
        return m_columnName;
    } // getColumnName(int)

    /**
     * Sets a new name for this column.
     * @param newName The new name or null to have no column name
     */
    public void setColumnName(final String newName) {
        m_columnName = newName;
        fireTableCellUpdated(TableModelEvent.HEADER_ROW, 0);
    }
    
    /** 
     * Set the table content to which this class will listen and whose
     * content is content of this class. 
     * 
     * <p>Note: If the passed argument is not an instance of 
     * <code>TableContentInterface</code>, this model will be a very dumb model:
     * It will not show any row keys and also no hilighting nor color 
     * information.  
     * @param content The new model.
     * @throws IllegalArgumentException If argument is null.
     */
    public void setTableContent(final TableModel content) {
        if (content == null) {
            throw new IllegalArgumentException("Can't set null content.");
        }
        if (content == m_contentInterface) {
            return;
        }
        // previously passed an TableModel that has been wrapped?
        if (m_contentInterface instanceof TableContentWrapper 
                && ((TableContentWrapper)m_contentInterface).m_model 
                == content) {
            return;
        }
        
        // set new value
        if (m_contentInterface != null) {
            m_contentInterface.removeTableModelListener(m_contentListener);
        }
        if (content instanceof TableContentInterface) {
            m_contentInterface = (TableContentInterface)content;
        } else {
            m_contentInterface = new TableContentWrapper(content);
        }
        m_contentInterface.addTableModelListener(m_contentListener);
        fireTableDataChanged();
    }

    
    /** 
     * Delegating method to <code>TableContentModel</code>.
     * @param row Row index of interest.
     * @return Highlight status of <code>row</code>.
     * @see TableContentModel#isHiLit(int)
     */
    public boolean isHiLit(final int row) {
        return m_contentInterface.isHiLit(row);
    } // isHiLit(int)

    /** 
     * Delegating method to <code>TableContentModel</code>.
     * @param row Row index of interest.
     * @return Color information to that row.
     * @see TableContentModel#getColorAttr(int)
     */
    public ColorAttr getColorAttr(final int row) {
        return m_contentInterface.getRowKey(row).getColorAttr();
    } // getColorAttr(int)

    /** 
     * Checks if column index is 0. If not, throws exception
     * @param columnIndex must be 0
     * @throws IndexOutOfBoundsException if index is not 0
     */
    private void boundColumn(final int columnIndex) {
        if (columnIndex != 0) {
            throw new IndexOutOfBoundsException("Column index for row header" 
                + "must be 0, not " + columnIndex);
        }
    } // boundColumn(int)
    
    private static class TableContentWrapper implements TableContentInterface {
        
        private static final RowKey UNKNOWN = 
            new RowKey(new DefaultStringCell("unknown"));
        private final TableModel m_model;
        
        /** Creates wrapper based on model.
         * @param model The model to wrap.
         */
        TableContentWrapper(final TableModel model) {
            m_model = model;
        }

        /**
         * Delegates to model.
         * @see TableContentInterface#getRowCount()
         */
        public int getRowCount() {
            return m_model.getRowCount();
        }

        /**
         * Returns "unknown".
         * @see TableContentInterface#getRowKey(int)
         */
        public RowKey getRowKey(final int row) {
            return UNKNOWN;
        }

        /**
         * Returns false.
         * @see TableContentInterface#isHiLit(int)
         */
        public boolean isHiLit(final int row) {
            return false;
        }

        /** Delegates to model.
         * @see TableContentInterface#addTableModelListener(TableModelListener)
         */
        public void addTableModelListener(final TableModelListener l) {
            m_model.addTableModelListener(l);
        }

        /**
         * Delegates to model.
         * @see TableContentInterface#removeTableModelListener(
         * TableModelListener)
         */
        public void removeTableModelListener(final TableModelListener l) {
            m_model.removeTableModelListener(l);
        }
    
    }
    
}
