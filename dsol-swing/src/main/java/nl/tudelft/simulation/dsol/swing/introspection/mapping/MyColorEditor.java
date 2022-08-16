package nl.tudelft.simulation.dsol.swing.introspection.mapping;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * Starts up a default {see javax.swing.JColorChooser}panel to edit the color value.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>.
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>.
 * @author Niels Lang.
 * @since 1.5
 */
public class MyColorEditor extends AbstractCellEditor implements TableCellEditor
{
    /** */
    private static final long serialVersionUID = 20140831L;

    /** the color. */
    protected Color color;

    /** the cellPanel. */
    protected JPanel cellPanel = new JPanel();

    /**
     * The OK listener.
     */
    private class OKListener implements ActionListener
    {
        /** the color chooser. */
        private JColorChooser chooser;

        /**
         * constructs a new OKListener.
         * @param chooser JColorChooser; the color chooser.
         */
        OKListener(final JColorChooser chooser)
        {
            this.chooser = chooser;
        }

        /** {@inheritDoc} */
        @Override
        public synchronized void actionPerformed(final ActionEvent event)
        {
            MyColorEditor.this.color = this.chooser.getColor();
            MyColorEditor.this.stopCellEditing();
            MyColorEditor.this.cellPanel.setBackground(MyColorEditor.this.color.darker());
            MyColorEditor.this.cellPanel.paintImmediately(MyColorEditor.this.cellPanel.getBounds());
        }
    }

    /**
     * The CancelListener.
     */
    protected class CancelListener implements ActionListener
    {
        /** {@inheritDoc} */
        @Override
        public void actionPerformed(final ActionEvent e)
        {
            MyColorEditor.this.cancelCellEditing();
        }
    }

    /** {@inheritDoc} */
    @Override
    public Object getCellEditorValue()
    {
        return this.color;
    }

    /** {@inheritDoc} */
    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
            final int row, final int column)
    {
        this.cellPanel.setBackground(((Color) value).darker());
        this.color = (Color) value;
        JColorChooser chooser = new JColorChooser((Color) value);
        JDialog dialog = JColorChooser.createDialog(table, "Color selection", false, chooser, new OKListener(chooser),
                new CancelListener());
        dialog.setVisible(true);
        return this.cellPanel;
    }
}
