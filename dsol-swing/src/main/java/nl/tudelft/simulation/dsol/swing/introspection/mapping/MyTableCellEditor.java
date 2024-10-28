package nl.tudelft.simulation.dsol.swing.introspection.mapping;

import java.awt.Color;
import java.awt.Component;
import java.util.EventObject;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

/**
 * An editor for TableObjects.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>.
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>.
 * @author Niels Lang.
 * @since 1.5
 */
public class MyTableCellEditor implements TableCellEditor
{
    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
            final int row, final int column)
    {
        JPanel result = new JPanel();
        result.setBackground(Color.YELLOW);
        return result;
    }

    @Override
    public Object getCellEditorValue()
    {
        return null;
    }

    @Override
    public boolean isCellEditable(final EventObject anEvent)
    {
        return false;
    }

    @Override
    public boolean shouldSelectCell(final EventObject anEvent)
    {
        return false;
    }

    @Override
    public boolean stopCellEditing()
    {
        return false;
    }

    @Override
    public void cancelCellEditing()
    {
        // We cannot edit; this method will never be invoked.
    }

    @Override
    public void addCellEditorListener(final CellEditorListener l)
    {
        // Strange, we do not edit
    }

    @Override
    public void removeCellEditorListener(final CellEditorListener l)
    {
        // Same story
    }
}
