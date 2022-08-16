/*
 * Created on Aug 26, 2004 @ Erasmus University Rotterdam Copyright (c) Delft
 * University of Technology
 */
package nl.tudelft.simulation.dsol.swing.introspection.mapping;

import java.awt.Component;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.djutils.immutablecollections.ImmutableMap;

/**
 * Renders Map values in a human-readable manner.
 * <p>
 * Copyright (c) 2018-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>.
 */
public class ImmutableMapRenderer extends DefaultTableCellRenderer
{
    /** */
    private static final long serialVersionUID = 20140831L;

    /** {@inheritDoc} */
    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
            final boolean hasFocus, final int row, final int column)
    {
        @SuppressWarnings("unchecked")
        Map<Object, Object> map = new LinkedHashMap<Object, Object>(((ImmutableMap<Object, Object>) value).toMap());
        String content = "ImmutableMap of <";
        if (map.size() > 0)
        {
            Object key = map.keySet().iterator().next();
            content += getShortName(key.getClass()) + "," + getShortName(map.get(key).getClass());
        }
        else
            content += "?,?";
        content += ">: ";
        Iterator<Object> keys = map.keySet().iterator();
        while (keys.hasNext())
        {
            Object key = keys.next();
            content += "{" + key.toString() + "," + map.get(key).toString() + "}; ";
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
