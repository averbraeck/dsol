/*
 * Created on Aug 26, 2004 @ Erasmus University Rotterdam Copyright (c) Delft
 * University of Technology
 */
package nl.tudelft.simulation.dsol.swing.introspection.mapping;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.djutils.immutablecollections.ImmutableCollection;

/**
 * Renders Collection values in a human-readable manner.
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
 */
public class ImmutableCollectionRenderer extends DefaultTableCellRenderer
{
    /** */
    private static final long serialVersionUID = 20140831L;

    /** {@inheritDoc} */
    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
            final boolean hasFocus, final int row, final int column)
    {
        List<Object> coll = new ArrayList<Object>();
        for (Object o : (ImmutableCollection<?>) value)
        {
            coll.add(o);
        }
        String content = "ImmutableCollection of ";
        if (coll.size() > 0)
        {
            content += getShortName(coll.get(0).getClass());
        }
        else
        {
            content += "?";
        }
        content += ": ";
        for (int i = 0; i < coll.size(); i++)
        {
            content += coll.get(i).toString() + "; ";
        }
        return new JLabel(content);
    }

    /**
     * Returns the short name of a class.
     * @param clasz Class&lt;?&gt;; the class
     * @return the short name
     */
    private static String getShortName(final Class<?> clasz)
    {
        String name = clasz.getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }
}
