package nl.tudelft.simulation.dsol.swing.introspection.mapping;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

/**
 * The default editor.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>.
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>.
 * @author Niels Lang.
 * @since 1.5
 */
public class MyDefaultEditor implements TableCellEditor
{
    /** the delegate. */
    private TableCellEditor delegate = new DefaultCellEditor(new JTextField());

    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
            final int row, final int column)
    {
        TableCellEditor editor = table.getDefaultEditor(value.getClass());
        if (!(editor instanceof MyDefaultEditor))
        {
            this.delegate = editor;
        }
        return this.delegate.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public void cancelCellEditing()
    {
        this.delegate.cancelCellEditing();
    }

    @Override
    public Object getCellEditorValue()
    {
        return this.delegate.getCellEditorValue();
    }

    @Override
    public boolean isCellEditable(final EventObject anEvent)
    {
        return this.delegate.isCellEditable(anEvent);
    }

    @Override
    public boolean shouldSelectCell(final EventObject anEvent)
    {
        return this.delegate.shouldSelectCell(anEvent);
    }

    @Override
    public boolean stopCellEditing()
    {
        return this.delegate.stopCellEditing();
    }

    @Override
    public void addCellEditorListener(final CellEditorListener l)
    {
        this.delegate.addCellEditorListener(l);
    }

    @Override
    public void removeCellEditorListener(final CellEditorListener l)
    {
        this.delegate.removeCellEditorListener(l);
    }
}
