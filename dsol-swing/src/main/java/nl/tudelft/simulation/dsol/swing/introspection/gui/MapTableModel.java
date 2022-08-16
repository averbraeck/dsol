package nl.tudelft.simulation.dsol.swing.introspection.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.djutils.immutablecollections.ImmutableMap;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.introspection.AbstractProperty;
import nl.tudelft.simulation.introspection.Introspector;
import nl.tudelft.simulation.introspection.Property;
import nl.tudelft.simulation.introspection.beans.BeanIntrospector;

/**
 * A tablemodel used to manage and present the instances of a map property.
 * <p>
 * Copyright (c) 2018-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>.
 */
public class MapTableModel extends AbstractTableModel implements IntrospectingTableModelInterface
{
    /** */
    private static final long serialVersionUID = 20140831L;

    /** the keys of the map entries. */
    protected Map<Integer, Object> keyMap = Collections.synchronizedMap(new LinkedHashMap<Integer, Object>(20));

    /** the values of the map entries. */
    protected Map<Integer, Object> valueMap = Collections.synchronizedMap(new LinkedHashMap<Integer, Object>(20));

    /** the keys identifying specific instances. */
    protected List<Integer> rowKeys = Collections.synchronizedList(new ArrayList<Integer>(20));

    /** the COLUMNS of this tabbleModel. */
    private static final String[] COLUMNS = {"#", "+", "Key", "Value"};

    /** the expand button. */
    private List<ExpandButton> buttons = Collections.synchronizedList(new ArrayList<ExpandButton>(20));

    /** the parentProperty */
    private Property parentProperty;

    /** the introspector. */
    private Introspector introspector;

    /** The model manager. */
    private ModelManager manager = new DefaultModelManager();

    /** The highest key currently allocated. */
    private int maxKey = 0;

    /**
     * constructs a new CollectionTableModel.
     * @param parentProperty Property; the parentPropert
     */
    public MapTableModel(final Property parentProperty)
    {
        this(parentProperty, new BeanIntrospector());
    }

    /**
     * constructs a new CollectionTableModel.
     * @param parentProperty Property; the parentProperty
     * @param introspector Introspector; the introspector to use
     */
    public MapTableModel(final Property parentProperty, final Introspector introspector)
    {
        Object values;
        try
        {
            values = parentProperty.getValue();
        }
        catch (Exception e)
        {
            values = new LinkedHashMap<>();
        }
        if (values instanceof Map)
        {
            Map<?, ?> map = (Map<?, ?>) values;
            for (Object key : map.keySet())
            {
                addValue(key, map.get(key));
            }
        }
        if (values instanceof ImmutableMap)
        {
            ImmutableMap<?, ?> map = (ImmutableMap<?, ?>) values;
            for (Object key : map.keySet())
            {
                addValue(key, map.get(key));
            }
        }
        this.parentProperty = parentProperty;
        this.introspector = introspector;
        // Initialize buttons
        for (int i = 0; i < this.keyMap.size(); i++)
        {
            ExpandButton button = new ExpandButton(getProperty(i), this);
            this.buttons.add(button);
        }
        this.fireTableDataChanged();
    }

    /**
     * Adds a new value to the managed composite property.
     * @param key Object; the key
     * @param value Object; the value to add
     */
    private void addValue(final Object key, final Object value)
    {
        Integer nextKey = Integer.valueOf(this.maxKey++);
        this.rowKeys.add(nextKey);
        this.keyMap.put(nextKey, key);
        this.valueMap.put(nextKey, value);
    }

    /** {@inheritDoc} */
    @Override
    public int getRowCount()
    {
        return this.keyMap.size();
    }

    /** {@inheritDoc} */
    @Override
    public int getColumnCount()
    {
        return MapTableModel.COLUMNS.length;
    }

    /** {@inheritDoc} */
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex)
    {
        if (columnIndex == 0)
        {
            return Integer.valueOf(rowIndex);
        }
        if (columnIndex == 1)
        {
            return this.buttons.get(rowIndex);
        }
        if (columnIndex == 2)
        {
            return this.keyMap.get(this.rowKeys.get(rowIndex));
        }
        if (columnIndex == 3)
        {
            return this.valueMap.get(this.rowKeys.get(rowIndex));
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getColumnName(final int columnIndex)
    {
        return MapTableModel.COLUMNS[columnIndex];
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex)
    {
        // NOTE: For a button to be clickable, it needs to be editable!!!
        if (columnIndex == 1)
        {
            return true;
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setValueAt(final Object keyValue, final int rowIndex, final int columnIndex)
    {
        if (columnIndex == 1)
            return; // Clicked a button...
        CategoryLogger.always().warn("cannot set values in a Map...");
        throw new IllegalArgumentException("cannot set values in a Map...");
    }

    /** {@inheritDoc} */
    @Override
    public Class<?> getColumnClass(final int columnIndex)
    {
        if (columnIndex == 1)
        {
            return ExpandButton.class;
        }
        return Object.class;
    }

    /**
     * The collection table model labels all properties according to their rowIndex. Only these labels are expected to be
     * requested here.
     * @see nl.tudelft.simulation.dsol.swing.introspection.gui.IntrospectingTableModelInterface #getProperty(java.lang.String)
     */
    @Override
    public Property getProperty(final String propertyName)
    {
        int index = Integer.parseInt(propertyName);
        return getProperty(index);
    }

    /**
     * @param index int; the index of the property
     * @return the Property
     */
    protected Property getProperty(final int index)
    {
        return new MapProperty(this.rowKeys.get(index), this.parentProperty.getName());
    }

    /** {@inheritDoc} */
    @Override
    public Introspector getIntrospector()
    {
        return this.introspector;
    }

    /** {@inheritDoc} */
    @Override
    public Class<?> getTypeAt(final int rowIndex, final int columnIndex)
    {
        if (columnIndex == 0)
        {
            return String.class;
        }
        if (columnIndex == 1)
        {
            return ExpandButton.class;
        }
        if (columnIndex == 2)
        {
            return this.keyMap.get(this.rowKeys.get(rowIndex)).getClass();
        }
        if (columnIndex == 3)
        {
            return this.valueMap.get(this.rowKeys.get(rowIndex)).getClass();
        }
        return null;
    }

    /**
     * Sets the modelmanager. By default, a {see DefaultModelManager}is used.
     * @param manager ModelManager; the manager
     */
    public void setModelManager(final ModelManager manager)
    {
        this.manager = manager;
    }

    /**
     * By default, a {see DefaultModelManager}returned.
     * @see nl.tudelft.simulation.dsol.swing.introspection.gui.IntrospectingTableModelInterface #getModelManager()
     * @return the Manager
     */
    @Override
    public ModelManager getModelManager()
    {
        return this.manager;
    }

    /**
     * The MapProperty.
     */
    class MapProperty extends AbstractProperty implements Property
    {
        /** the key of this property. */
        private final Integer key;

        /** the name. */
        private final String name;

        /**
         * This implementation is NOT thread-safe. When multiple users will edit the parent at the same time, errors are
         * expected.
         * @param key Integer; the key
         * @param name String; the name
         */
        MapProperty(final Integer key, final String name)
        {
            this.key = key;
            this.name = name;
        }

        /** {@inheritDoc} */
        @Override
        public Object getInstance()
        {
            return MapTableModel.this.valueMap.values();
        }

        /** {@inheritDoc} */
        @Override
        public String getName()
        {
            return this.name + "[" + MapTableModel.this.rowKeys.indexOf(this.key) + "]";
        }

        /** {@inheritDoc} */
        @Override
        public Class<?> getType()
        {
            return MapTableModel.this.valueMap.get(this.key).getClass();
        }

        /** {@inheritDoc} */
        @Override
        public Object getValue()
        {
            try
            {
                return MapTableModel.this.valueMap.get(this.key);
            }
            catch (Exception e)
            {
                return new String("-");
            }
        }

        /** {@inheritDoc} */
        @Override
        public boolean isEditable()
        {
            return false;
        }

        /** {@inheritDoc} */
        @Override
        protected void setRegularValue(final Object value)
        {
            throw new IllegalArgumentException(this + " is only supposed to be" + " set to composite values."
                    + "A program is not supposed to arrive here.");
        }

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            return "Map.Prop, key:" + this.key;
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "MapTableModel";
    }
}
