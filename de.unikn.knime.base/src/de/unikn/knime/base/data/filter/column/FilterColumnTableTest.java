/*
 * @(#)$RCSfile$ 
 * $Revision$ $Date$ $Author$
 *
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
package de.unikn.knime.base.data.filter.column;

import junit.framework.TestCase;
import de.unikn.knime.core.data.DataCell;
import de.unikn.knime.core.data.DataRow;
import de.unikn.knime.core.data.DataTable;
import de.unikn.knime.core.data.DataTableSpec;
import de.unikn.knime.core.data.DataType;
import de.unikn.knime.core.data.DoubleType;
import de.unikn.knime.core.data.IntType;
import de.unikn.knime.core.data.RowIterator;
import de.unikn.knime.core.data.StringType;
import de.unikn.knime.core.data.def.DefaultRow;
import de.unikn.knime.core.data.def.DefaultStringCell;

/**
 * JUnit test class for the filter column table which test column indices,
 * names, types, and its exceptions.
 * 
 * @author Thomas Gabriel, University of Konstanz
 * 
 * @see FilterColumnTable
 */
public final class FilterColumnTableTest extends TestCase {

    /**
     * Test table class used for column filtering.
     */
    private final class MyTestTable implements DataTable {
        private final DataTableSpec m_spec = new DataTableSpec(new DataCell[] {
                new DefaultStringCell("Col_A"), new DefaultStringCell("Col_B"),
                new DefaultStringCell("Col_C"), new DefaultStringCell("Col_D"),
                new DefaultStringCell("Col_E")}, new DataType[] {
                DoubleType.DOUBLE_TYPE, StringType.STRING_TYPE,
                IntType.INT_TYPE, DoubleType.DOUBLE_TYPE,
                StringType.STRING_TYPE});

        /**
         * @see de.unikn.knime.core.data.DataTable#getDataTableSpec()
         */
        public DataTableSpec getDataTableSpec() {
            return m_spec;
        }

        /**
         * @see de.unikn.knime.core.data.DataTable#iterator()
         */
        public RowIterator iterator() {
            return new MyTestRowIterator(m_spec.getNumColumns());
        }
    } // MyTestTable

    /**
     * Iternal row iterator which holds an array of <b>42 </b> rows along with a
     * number of column retrieved from the <code>DataTableSpec</code>.
     */
    private final class MyTestRowIterator extends RowIterator {
        private static final int ROWS = 42;

        private final DataRow[] m_rows = new DataRow[ROWS];

        private int m_index;

        /**
         * Creates a new iterator with the given number of columns. The row key
         * is formed as <i>row_ </i> plus row index. Each value is a string such
         * as <i>["row","column"] </i>.
         * 
         * @param columns The number of columns to generate.
         */
        MyTestRowIterator(final int columns) {
            for (int i = 0; i < m_rows.length; i++) {
                String[] cells = new String[columns];
                for (int c = 0; c < columns; c++) {
                    cells[c] = "[" + i + "," + c + "]";
                }
                m_rows[i] = new DefaultRow(new DefaultStringCell("row_" + i),
                        cells);
            }
            m_index = 0;
        }

        /**
         * @see de.unikn.knime.core.data.RowIterator#hasNext()
         */
        public boolean hasNext() {
            return (m_index != m_rows.length);
        }

        /**
         * @see de.unikn.knime.core.data.RowIterator#next()
         */
        public DataRow next() {
            return m_rows[m_index++];
        }
    }

    /*
     * Keep table for filtering.
     */
    private DataTable m_table;

    /**
     * Init internal members.
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        // init test table
        m_table = new MyTestTable();
    }

    /**
     * Destroy internal members.
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        m_table = null;
    }

    /*
     * Invoked on each testXXX() method to test all rows and cells on equality
     * by iterating through the entire table, that is, the filter as well as the
     * original data table. @param The filter table to test equality on.
     */
    private void tableTest(final FilterColumnTable f) {
        final int[] columns = f.getColumnIndices();
        RowIterator fIt = f.iterator();
        RowIterator tIt = m_table.iterator();
        for (; fIt.hasNext() && tIt.hasNext();) {
            DataRow rf = fIt.next();
            DataRow rt = tIt.next();
            // check also if the same rows are compared
            assertTrue(rf.getKey().equals(rt.getKey()));
            for (int i = 0; i < columns.length; i++) {
                // check cell from original with the mapped one
                assertTrue(rf.getCell(i).equals(rt.getCell(columns[i])));
            }
        }
    }

    /**
     * Test all available column indices.
     */
    public void testInConstructorColumnIndices1() {
        FilterColumnTable filter = new FilterColumnTable(m_table, new int[] {0,
                1, 2, 3, 4});
        DataTableSpec fSpec = filter.getDataTableSpec();
        assertTrue(fSpec.getNumColumns() == 5);
        assertTrue(fSpec.getColumnSpec(0).getName().equals(
                new DefaultStringCell("Col_A")));
        assertTrue(fSpec.getColumnSpec(0).getType().equals(
                DoubleType.DOUBLE_TYPE));
        assertTrue(fSpec.getColumnSpec(1).getName().equals(
                new DefaultStringCell("Col_B")));
        assertTrue(fSpec.getColumnSpec(1).getType().equals(
                StringType.STRING_TYPE));
        assertTrue(fSpec.getColumnSpec(2).getName().equals(
                new DefaultStringCell("Col_C")));
        assertTrue(fSpec.getColumnSpec(2).getType().equals(IntType.INT_TYPE));
        assertTrue(fSpec.getColumnSpec(3).getName().equals(
                new DefaultStringCell("Col_D")));
        assertTrue(fSpec.getColumnSpec(3).getType().equals(
                DoubleType.DOUBLE_TYPE));
        assertTrue(fSpec.getColumnSpec(4).getName().equals(
                new DefaultStringCell("Col_E")));
        assertTrue(fSpec.getColumnSpec(4).getType().equals(
                StringType.STRING_TYPE));
        // test the filter table
        tableTest(filter);
    }

    /**
     * Test all available column indices reverse ordered.
     */
    public void testInConstructorColumnIndices2() {
        FilterColumnTable filter = new FilterColumnTable(m_table, new int[] {4,
                3, 2, 1, 0});
        DataTableSpec fSpec = filter.getDataTableSpec();
        assertTrue(fSpec.getNumColumns() == 5);
        assertTrue(fSpec.getColumnSpec(4).getName().equals(
                new DefaultStringCell("Col_A")));
        assertTrue(fSpec.getColumnSpec(4).getType().equals(
                DoubleType.DOUBLE_TYPE));
        assertTrue(fSpec.getColumnSpec(3).getName().equals(
                new DefaultStringCell("Col_B")));
        assertTrue(fSpec.getColumnSpec(3).getType().equals(
                StringType.STRING_TYPE));
        assertTrue(fSpec.getColumnSpec(2).getName().equals(
                new DefaultStringCell("Col_C")));
        assertTrue(fSpec.getColumnSpec(2).getType().equals(IntType.INT_TYPE));
        assertTrue(fSpec.getColumnSpec(1).getName().equals(
                new DefaultStringCell("Col_D")));
        assertTrue(fSpec.getColumnSpec(1).getType().equals(
                DoubleType.DOUBLE_TYPE));
        assertTrue(fSpec.getColumnSpec(0).getName().equals(
                new DefaultStringCell("Col_E")));
        assertTrue(fSpec.getColumnSpec(0).getType().equals(
                StringType.STRING_TYPE));
        // test the filter table
        tableTest(filter);
    }

    /**
     * Test filtering one column by index.
     */
    public void testInConstructorColumnIndices3() {
        FilterColumnTable filter = 
            new FilterColumnTable(m_table, new int[] {3});
        DataTableSpec fSpec = filter.getDataTableSpec();
        assertTrue(fSpec.getNumColumns() == 1);
        assertTrue(fSpec.getColumnSpec(0).getName().equals(
                new DefaultStringCell("Col_D")));
        assertTrue(fSpec.getColumnSpec(0).getType().equals(
                DoubleType.DOUBLE_TYPE));
        // test the filter table
        tableTest(filter);
    }

    /**
     * Test filtering <code>DoubleCell</code> types which includes
     * <code>IntCell</code> types.
     */
    public void testInConstructorColumnTypes1() {
        FilterColumnTable filter = new FilterColumnTable(m_table,
                DoubleType.DOUBLE_TYPE);
        DataTableSpec fSpec = filter.getDataTableSpec();
        assertTrue(fSpec.getNumColumns() == 3);
        assertTrue(fSpec.getColumnSpec(0).getName().equals(
                new DefaultStringCell("Col_A")));
        assertTrue(fSpec.getColumnSpec(0).getType().equals(
                DoubleType.DOUBLE_TYPE));
        assertTrue(fSpec.getColumnSpec(1).getName().equals(
                new DefaultStringCell("Col_C")));
        assertTrue(fSpec.getColumnSpec(1).getType().equals(
                IntType.INT_TYPE));
        assertTrue(fSpec.getColumnSpec(2).getName().equals(
                new DefaultStringCell("Col_D")));
        assertTrue(fSpec.getColumnSpec(2).getType().equals(
                DoubleType.DOUBLE_TYPE));
        // test the filter table
        tableTest(filter);
    }

    /**
     * Test filtering <code>StringCell</code> types.
     */
    public void testInConstructorColumnTypes2() {
        FilterColumnTable filter = new FilterColumnTable(m_table,
                StringType.STRING_TYPE);
        DataTableSpec fSpec = filter.getDataTableSpec();
        assertTrue(fSpec.getNumColumns() == 2);
        assertTrue(fSpec.getColumnSpec(0).getName().equals(
                new DefaultStringCell("Col_B")));
        assertTrue(fSpec.getColumnSpec(0).getType().equals(
                StringType.STRING_TYPE));
        assertTrue(fSpec.getColumnSpec(1).getName().equals(
                new DefaultStringCell("Col_E")));
        assertTrue(fSpec.getColumnSpec(1).getType().equals(
                StringType.STRING_TYPE));
        // test the filter table
        tableTest(filter);
    }

    /**
     * Test filtering <code>IntCell</code> types.
     */
    public void testInConstructorColumnTypes3() {
        FilterColumnTable filter = new FilterColumnTable(m_table,
                IntType.INT_TYPE);
        DataTableSpec fSpec = filter.getDataTableSpec();
        assertTrue(fSpec.getNumColumns() == 1);
        assertTrue(fSpec.getColumnSpec(0).getName().equals(
                new DefaultStringCell("Col_C")));
        assertTrue(fSpec.getColumnSpec(0).getType().equals(IntType.INT_TYPE));
        // test the filter table
        tableTest(filter);
    }

    /**
     * Test all available column names.
     */
    public void testInConstructorColumnNames1() {
        FilterColumnTable filter = new FilterColumnTable(m_table,
                new DataCell[] {new DefaultStringCell("Col_A"),
                        new DefaultStringCell("Col_B"),
                        new DefaultStringCell("Col_C"),
                        new DefaultStringCell("Col_D"),
                        new DefaultStringCell("Col_E")});
        DataTableSpec fSpec = filter.getDataTableSpec();
        assertTrue(fSpec.getNumColumns() == 5);
        assertTrue(fSpec.getColumnSpec(0).getName().equals(
                new DefaultStringCell("Col_A")));
        assertTrue(fSpec.getColumnSpec(0).getType().equals(
                DoubleType.DOUBLE_TYPE));
        assertTrue(fSpec.getColumnSpec(1).getName().equals(
                new DefaultStringCell("Col_B")));
        assertTrue(fSpec.getColumnSpec(1).getType().equals(
                StringType.STRING_TYPE));
        assertTrue(fSpec.getColumnSpec(2).getName().equals(
                new DefaultStringCell("Col_C")));
        assertTrue(fSpec.getColumnSpec(2).getType().equals(
                IntType.INT_TYPE));
        assertTrue(fSpec.getColumnSpec(3).getName().equals(
                new DefaultStringCell("Col_D")));
        assertTrue(fSpec.getColumnSpec(3).getType().equals(
                DoubleType.DOUBLE_TYPE));
        assertTrue(fSpec.getColumnSpec(4).getName().equals(
                new DefaultStringCell("Col_E")));
        assertTrue(fSpec.getColumnSpec(4).getType().equals(
                StringType.STRING_TYPE));
        // test the filter table
        tableTest(filter);
    }

    /**
     * Test filtering randomly column names.
     */
    public void testInConstructorColumnNames2() {
        FilterColumnTable filter = new FilterColumnTable(m_table,
                new DataCell[] {new DefaultStringCell("Col_E"),
                        new DefaultStringCell("Col_C"),
                        new DefaultStringCell("Col_D"),
                        new DefaultStringCell("Col_A")});
        DataTableSpec fSpec = filter.getDataTableSpec();
        assertTrue(fSpec.getNumColumns() == 4);
        assertTrue(fSpec.getColumnSpec(3).getName().equals(
                new DefaultStringCell("Col_A")));
        assertTrue(fSpec.getColumnSpec(3).getType().equals(
                DoubleType.DOUBLE_TYPE));
        assertTrue(fSpec.getColumnSpec(1).getName().equals(
                new DefaultStringCell("Col_C")));
        assertTrue(fSpec.getColumnSpec(1).getType().equals(
                IntType.INT_TYPE));
        assertTrue(fSpec.getColumnSpec(2).getName().equals(
                new DefaultStringCell("Col_D")));
        assertTrue(fSpec.getColumnSpec(2).getType().equals(
                DoubleType.DOUBLE_TYPE));
        assertTrue(fSpec.getColumnSpec(0).getName().equals(
                new DefaultStringCell("Col_E")));
        assertTrue(fSpec.getColumnSpec(0).getType().equals(
                StringType.STRING_TYPE));
        // test the filter table
        tableTest(filter);
    }

    /**
     * Table is <code>null</code>.
     */
    public void testInConstructorException1() {
        try {
            new FilterColumnTable(null, new int[] {0});
            fail("Exception expected: Table is null.");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    /**
     * Array of column indices is <code>null</code>.
     */
    public void testInConstructorException2() {
        try {
            new FilterColumnTable(m_table, (int[])null);
            fail("Exception expected: Column indices are null.");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    /**
     * Array of colum indices is empty.
     */
    public void testInConstructorException3() {
        new FilterColumnTable(m_table, new int[] {});
        assertTrue(true);
    }

    /**
     * Column index in array appears twice.
     */
    public void testInConstructorException4() {
        try {
            new FilterColumnTable(m_table, new int[] {0, 1, 0});
            fail("Exception expected: Column index 0 found twice.");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    /**
     * Negative column index in array.
     */
    public void testInConstructorException5() {
        try {
            new FilterColumnTable(m_table, new int[] {0, 1, -1});
            fail("Exception expected: Negative column index -1.");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    /**
     * Array column index out of range.
     */
    public void testInConstructorException6() {
        try {
            new FilterColumnTable(m_table, new int[] {0, 1, 2, 3, 4, 5});
            fail("Exception expected: Column index out of range 5.");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    /**
     * Class type is <code>null</code>.
     */
    public void testInConstructorException7() {
        try {
            new FilterColumnTable(m_table, (DataType)null);
            fail("Exception expected: Class type is null.");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    /**
     * Array of column names is empty.
     */
    public void testInConstructorException10() {
        try {
            new FilterColumnTable(m_table, (DataCell[])null);
            fail("Exception expected: Array of column names is null.");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    /**
     * Array of column indices is empty.
     */
    public void testInConstructorException11() {
        new FilterColumnTable(m_table, new DataCell[] {});
        assertTrue(true);
    }

    /**
     * Column name not in de.unikn.knime.core.data.
     */
    public void testInConstructorException12() {
        try {
            new FilterColumnTable(m_table,
                    new DataCell[] {new DefaultStringCell("Bla")});
            fail("Exception expected: Column name \"Bla\" not in data.");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    /**
     * Column name twice in de.unikn.knime.core.data.
     */
    public void testInConstructorException13() {
        try {
            new FilterColumnTable(m_table, new DataCell[] {
                    new DefaultStringCell("Col_A"),
                    new DefaultStringCell("Col_A")});
            fail("Exception expected: Column name \"Col_A\" found twice.");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    /**
     * Test all available column indices.
     */
    public void testInExConstructorColumnIndices1() {
        FilterColumnTable filter = new FilterColumnTable(m_table, false, 
                new int[] {0, 1, 2, 3, 4});
        DataTableSpec fSpec = filter.getDataTableSpec();
        assertTrue(fSpec.getNumColumns() == 0);
        // test the filter table
        tableTest(filter);
    }

    /**
     * Test all available column indices reverse ordered.
     */
    public void testInExConstructorColumnIndices2() {
        FilterColumnTable filter = new FilterColumnTable(m_table, false, 
                new int[] {4, 3, 2, 1, 0});
        DataTableSpec fSpec = filter.getDataTableSpec();
        assertTrue(fSpec.getNumColumns() == 0);
        // test the filter table
        tableTest(filter);
    }

    /**
     * Test filtering one column by index.
     */
    public void testInExConstructorColumnIndices3() {
        FilterColumnTable filter = new FilterColumnTable(m_table, false, 3);
        DataTableSpec fSpec = filter.getDataTableSpec();
        assertTrue(fSpec.getNumColumns() == 4);
        assertTrue(fSpec.getColumnSpec(0).getName().equals(
                new DefaultStringCell("Col_A")));
        assertTrue(fSpec.getColumnSpec(0).getType().equals(
                DoubleType.DOUBLE_TYPE));
        assertTrue(fSpec.getColumnSpec(1).getName().equals(
                new DefaultStringCell("Col_B")));
        assertTrue(fSpec.getColumnSpec(1).getType().equals(
                StringType.STRING_TYPE));
        assertTrue(fSpec.getColumnSpec(2).getName().equals(
                new DefaultStringCell("Col_C")));
        assertTrue(fSpec.getColumnSpec(2).getType().equals(
                IntType.INT_TYPE));
        assertTrue(fSpec.getColumnSpec(3).getName().equals(
                new DefaultStringCell("Col_E")));
        assertTrue(fSpec.getColumnSpec(3).getType().equals(
                StringType.STRING_TYPE));
        // test the filter table
        tableTest(filter);
    }

    /**
     * Table is <code>null</code>.
     */
    public void testInExConstructorException1() {
        try {
            new FilterColumnTable(null, false, 0);
            fail("Exception expected: Table is null.");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    /**
     * Array of column indices is <code>null</code>.
     */
    public void testInExConstructorException2() {
        try {
            new FilterColumnTable(m_table, false, (int[])null);
            fail("Exception expected: Column indices are null.");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    /**
     * Array of colum indices is empty.
     */
    public void testInExConstructorException3() {
        new FilterColumnTable(m_table, false, new int[] {});
        assertTrue(true);
    }

    /**
     * Column index in array appears twice.
     */
    public void testInExConstructorException4() {
        try {
            new FilterColumnTable(m_table, false, new int[] {0, 1, 0});
            fail("Exception expected: Column index 0 found twice.");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    /**
     * Negative column index in array.
     */
    public void testInExConstructorException5() {
        try {
            new FilterColumnTable(m_table, false, new int[] {0, 1, -1});
            fail("Exception expected: Negative column index -1.");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    /**
     * Array column index out of range.
     */
    public void testInExConstructorException6() {
        try {
            new FilterColumnTable(m_table, false, 
                    new int[] {0, 1, 2, 3, 4, 5});
            fail("Exception expected: Column index out of range 5.");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    /**
     * System entry point.
     * 
     * @param args The command line parameters: ignored.
     */
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(FilterColumnTableTest.class);
    }

} // FilterColumnTableTest
