package nl.tudelft.simulation.dsol.swing.introspection.gui;

import javax.swing.table.TableModel;

import nl.tudelft.simulation.dsol.swing.introspection.sortable.SortingTableModel;
import nl.tudelft.simulation.introspection.Introspector;
import nl.tudelft.simulation.introspection.Property;

/**
 * The sortingObjectTableModel. Can act as a delegate for an instance of {see
 * nl.tudelft.simulation.introspection.gui.IntrospectingTableModelInterface}.
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
public class SortingObjectTableModel extends SortingTableModel implements IntrospectingTableModelInterface
{
    /**
     * constructs a new SortingObjectTableModel.
     * @param source TableModel; the source of this tableModel
     */
    public SortingObjectTableModel(final TableModel source)
    {
        super(source);
    }

    /** {@inheritDoc} */
    @Override
    public Introspector getIntrospector()
    {
        if (!(this.source instanceof IntrospectingTableModelInterface))
        {
            return null;
        }
        return ((IntrospectingTableModelInterface) this.source).getIntrospector();
    }

    /** {@inheritDoc} */
    @Override
    public Property getProperty(final String propertyName)
    {
        if (!(this.source instanceof IntrospectingTableModelInterface))
        {
            return null;
        }
        return ((IntrospectingTableModelInterface) this.source).getProperty(propertyName);
    }

    /** {@inheritDoc} */
    @Override
    public Class<?> getTypeAt(final int rowIndex, final int columnIndex)
    {
        if (!(this.source instanceof IntrospectingTableModelInterface))
        {
            return null;
        }
        return ((IntrospectingTableModelInterface) this.source).getTypeAt(this.expandedIndex[rowIndex].intValue(), columnIndex);
    }

    /** {@inheritDoc} */
    @Override
    public ModelManager getModelManager()
    {
        if (!(this.source instanceof IntrospectingTableModelInterface))
        {
            return null;
        }
        return ((IntrospectingTableModelInterface) this.source).getModelManager();
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "SortingObjectTableModel";
    }
}
