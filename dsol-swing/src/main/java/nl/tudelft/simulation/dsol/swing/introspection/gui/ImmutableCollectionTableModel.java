package nl.tudelft.simulation.dsol.swing.introspection.gui;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.djutils.immutablecollections.ImmutableCollection;

import nl.tudelft.simulation.introspection.AbstractProperty;
import nl.tudelft.simulation.introspection.Introspector;
import nl.tudelft.simulation.introspection.Property;
import nl.tudelft.simulation.introspection.beans.BeanIntrospector;

/**
 * A tablemodel used to manage and present the instances of a composite property.
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
public class ImmutableCollectionTableModel extends AbstractTableModel implements IntrospectingTableModelInterface
{
    /** */
    private static final long serialVersionUID = 20140831L;

    /** the instances of the collection. */
    protected Map<Integer, Object> instances = Collections.synchronizedMap(new LinkedHashMap<Integer, Object>(20));

    /** the keys identifying specific instances. */
    protected List<Integer> keys = Collections.synchronizedList(new ArrayList<Integer>(20));

    /** the COLUMNS of this tabbleModel. */
    private static final String[] COLUMNS = {"#", "+", "Instance"};

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
    public ImmutableCollectionTableModel(final Property parentProperty)
    {
        this(parentProperty, new BeanIntrospector());
    }

    /**
     * constructs a new CollectionTableModel.
     * @param parentProperty Property; the parentProperty
     * @param introspector Introspector; the introspector to use
     */
    public ImmutableCollectionTableModel(final Property parentProperty, final Introspector introspector)
    {
        Object values;
        try
        {
            values = parentProperty.getValue();
        }
        catch (Exception e)
        {
            values = new String("-");
        }
        if (values.getClass().isArray())
        {
            for (int i = 0; i < Array.getLength(values); i++)
            {
                addValue(Array.get(values, i));
            }
        }
        if (values instanceof Collection)
        {
            for (Iterator<?> i = ((Collection<?>) values).iterator(); i.hasNext();)
            {
                addValue(i.next());
            }
        }
        if (values instanceof ImmutableCollection)
        {
            for (Iterator<?> i = ((ImmutableCollection<?>) values).iterator(); i.hasNext();)
            {
                addValue(i.next());
            }
        }
        this.parentProperty = parentProperty;
        this.introspector = introspector;
        // Initialize buttons
        for (int i = 0; i < this.instances.size(); i++)
        {
            this.buttons.add(new ExpandButton(getProperty(i), this));
        }
    }

    /**
     * Adds a new value to the managed composite property.
     * @param value Object; the value to add
     */
    private void addValue(final Object value)
    {
        Integer nextKey = Integer.valueOf(this.maxKey++);
        this.keys.add(nextKey);
        this.instances.put(nextKey, value);
    }

    /** {@inheritDoc} */
    @Override
    public int getRowCount()
    {
        return this.instances.size();
    }

    /** {@inheritDoc} */
    @Override
    public int getColumnCount()
    {
        return ImmutableCollectionTableModel.COLUMNS.length;
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
            return this.instances.get(this.keys.get(rowIndex));
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getColumnName(final int columnIndex)
    {
        return ImmutableCollectionTableModel.COLUMNS[columnIndex];
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
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex)
    {
        if (columnIndex == 1)
            return; // Clicked a button...
        throw new IllegalArgumentException("cannot set values in an ImmutableCollection...");
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
        return new ImmutableCollectionProperty(this.keys.get(index), this.parentProperty.getName());
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
            return this.instances.get(this.keys.get(rowIndex)).getClass();
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
     * The ImmutableCollectionProperty.
     */
    class ImmutableCollectionProperty extends AbstractProperty implements Property
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
        ImmutableCollectionProperty(final Integer key, final String name)
        {
            this.key = key;
            this.name = name;
        }

        /** {@inheritDoc} */
        @Override
        public Object getInstance()
        {
            return ImmutableCollectionTableModel.this.instances.values();
        }

        /** {@inheritDoc} */
        @Override
        public String getName()
        {
            return this.name + "[" + ImmutableCollectionTableModel.this.keys.indexOf(this.key) + "]";
        }

        /** {@inheritDoc} */
        @Override
        public Class<?> getType()
        {
            return ImmutableCollectionTableModel.this.instances.get(this.key).getClass();
        }

        /** {@inheritDoc} */
        @Override
        public Object getValue()
        {
            try
            {
                return ImmutableCollectionTableModel.this.instances.get(this.key);
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
            return "ImmColl.Prop, key:" + this.key;
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "ImmutableCollectionTableModel";
    }
}
