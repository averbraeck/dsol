package nl.tudelft.simulation.dsol.swing.introspection.mapping;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.swing.introspection.gui.ExpandButton;

/**
 * Implements the pop-up behaviour of the {see nl.tudelft.simulation.introspection.gui.ExpandButton}.
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
public class ExpandButtonEditor extends AbstractCellEditor implements TableCellEditor
{
    /** */
    private static final long serialVersionUID = 20140831L;

    /** the value. */
    private JComponent component;

    /** {@inheritDoc} */
    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
            final int row, final int column)
    {
        if (value instanceof ExpandButton)
        {
            ((ExpandButton) value).setMyJTable(table);
        }
        else
        {
            CategoryLogger.always().warn("getTableCellEditorComponent: Expected value to be an ExpandButton, but found: {}",
                    value);
        }
        return (Component) value;
    }

    /** {@inheritDoc} */
    @Override
    public Object getCellEditorValue()
    {
        return this.component;
    }
}
