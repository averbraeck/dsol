package nl.tudelft.simulation.dsol.swing.introspection.gui;

import java.util.Arrays;

import javax.swing.table.AbstractTableModel;

import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.introspection.Introspector;
import nl.tudelft.simulation.introspection.Property;
import nl.tudelft.simulation.introspection.beans.BeanIntrospector;

/**
 * The ObjectTableModel. <br>
 * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology</a>. <br>
 * BSD-style license. See <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank"> DSOL License</a>. <br>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>.
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>.
 * @author Niels Lang.
 * @since 1.5
 */
public class ObjectTableModel extends AbstractTableModel implements IntrospectingTableModelInterface
{
    /** */
    private static final long serialVersionUID = 20140831L;

    /** the properties. */
    protected Property[] properties = new Property[0];

    /** the columns. */
    private static String[] columns = {"Property", "+", "Value"};

    /** the expand buttons. */
    private ExpandButton[] buttons;

    /** the introspector. */
    private Introspector introspector = null;

    /** The model manager. */
    private ModelManager manager = new DefaultModelManager();

    /**
     * Creates an ObjectTableModel utilizing a {see nl.tudelft.simulation.introspection.beans.BeanIntrospector}.
     * @param bean Object; The object to be introspected according to the bean property-paradigm.
     */
    public ObjectTableModel(final Object bean)
    {
        this(bean, new BeanIntrospector());
    }

    /**
     * Creates an ObjectTableModel utilizing a custom introspector.
     * @param object Object; The object to be introspected.
     * @param introspector Introspector; The introspector instance utilized.
     */
    public ObjectTableModel(final Object object, final Introspector introspector)
    {
        this.properties = introspector.getProperties(object);
        this.buttons = new ExpandButton[this.properties.length];
        for (int i = 0; i < this.buttons.length; i++)
        {
            this.buttons[i] = new ExpandButton(this.properties[i], this);
        }
        this.introspector = introspector;
    }

    /** {@inheritDoc} */
    @Override
    public int getRowCount()
    {
        return this.properties.length;
    }

    /** {@inheritDoc} */
    @Override
    public int getColumnCount()
    {
        return columns.length;
    }

    /** {@inheritDoc} */
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex)
    {
        Property requested = this.properties[rowIndex];
        if (columnIndex == 0)
        {
            return requested.getName();
        }
        if (columnIndex == 1)
        {
            return this.buttons[rowIndex];
        }
        if (columnIndex == 2)
        {
            try
            {
                return requested.getValue();
            }
            catch (Exception e)
            {
                // something went wrong retrieving this value, e.g. because object was deleted.
                return new String("-");
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getColumnName(final int columnIndex)
    {
        return columns[columnIndex];
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex)
    {
        if (columnIndex == 1)
        {
            return true;
        }
        if (columnIndex == 2)
        {
            return (this.properties[rowIndex].isEditable() && !this.properties[rowIndex].getType().isArray());
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex)
    {
        if ((columnIndex != 2) || (!isCellEditable(rowIndex, columnIndex)))
        {
            return;
        }
        Property requested = this.properties[rowIndex];
        try
        {
            requested.setValue(aValue);
        }
        catch (IllegalArgumentException exception)
        {
            CategoryLogger.always().warn(exception, "setValueAt");
        }
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

    /** {@inheritDoc} */
    @Override
    public Class<?> getTypeAt(final int rowIndex, final int columnIndex)
    {
        Property requested = this.properties[rowIndex];
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
            return requested.getType();
        }
        return null;
    }

    /**
     * @param property String; the property
     * @return Returns the index of the property in this tablemodel which name matches 'property'.
     */
    protected int getPropertyIndex(final String property)
    {
        for (int i = 0; i < this.properties.length; i++)
        {
            if (this.properties[i].getName().equalsIgnoreCase(property))
            {
                return i;
            }
        }
        return -1;
    }

    /** {@inheritDoc} */
    @Override
    public Property getProperty(final String propertyName)
    {
        int index = getPropertyIndex(propertyName);
        if (index == -1)
        {
            return null;
        }
        return this.properties[index];
    }

    /** {@inheritDoc} */
    @Override
    public Introspector getIntrospector()
    {
        return this.introspector;
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
     */
    @Override
    public ModelManager getModelManager()
    {
        return this.manager;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "ObjectTableModel [properties=" + Arrays.toString(this.properties) + "]";
    }

}
