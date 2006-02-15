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
package de.unikn.knime.base.node.io.filereader;

import de.unikn.knime.core.node.NodeDialogPane;
import de.unikn.knime.core.node.NodeFactory;
import de.unikn.knime.core.node.NodeModel;
import de.unikn.knime.core.node.NodeView;

/**
 * @author ohl University of Konstanz
 */
public class FileReaderNodeFactory extends NodeFactory {

    private String m_defaultXMLFile;

    /**
     * @param defXMLFileName this string will be set as default path to a XML
     *            file containing settings for the dialog. Won't be supported in
     *            the future anymore. _@_deprecated
     */
    public FileReaderNodeFactory(final String defXMLFileName) {
        m_defaultXMLFile = defXMLFileName;
    }

    /**
     * default constructor for the Filereader node factory.
     */
    public FileReaderNodeFactory() {
        m_defaultXMLFile = null;
    }

    /**
     * @see de.unikn.knime.core.node.NodeFactory#getNodeName()
     */
    public String getNodeName() {
        return "File Reader";
    }

    /**
     * @see de.unikn.knime.core.node.NodeFactory#createNodeModel()
     */
    public NodeModel createNodeModel() {
        if (m_defaultXMLFile == null) {
            return new FileReaderNodeModel();
        } else {
            return new FileReaderNodeModel(m_defaultXMLFile);
        }
    }

    /**
     * @see de.unikn.knime.core.node.NodeFactory#getNrNodeViews()
     */
    public int getNrNodeViews() {
        return 0;
    }

    /**
     * @see de.unikn.knime.core.node.NodeFactory#createNodeView(int,NodeModel)
     */
    public NodeView createNodeView(final int i, final NodeModel nodeModel) {
        throw new IllegalStateException();
    }

    /**
     * @return <b>true </b>.
     * @see de.unikn.knime.core.node.NodeFactory#hasDialog()
     */
    public boolean hasDialog() {
        return true;
    }

    /**
     * @see de.unikn.knime.core.node.NodeFactory#createNodeDialogPane()
     */
    public NodeDialogPane createNodeDialogPane() {
        return new FileReaderNodeDialog();
    }

}
