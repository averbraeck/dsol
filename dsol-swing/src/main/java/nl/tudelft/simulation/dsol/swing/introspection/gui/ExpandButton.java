package nl.tudelft.simulation.dsol.swing.introspection.gui;

import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.swing.introspection.mapping.CellPresentationConfiguration;
import nl.tudelft.simulation.dsol.swing.introspection.mapping.DefaultConfiguration;
import nl.tudelft.simulation.introspection.Introspector;
import nl.tudelft.simulation.introspection.Property;

/**
 * A table-element that spawns an introspection dialog for a property. In the new dialog, the property has become the
 * introspected object.
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
public class ExpandButton extends JButton
{
    /** */
    private static final long serialVersionUID = 20140831L;

    /** the JTable in which this button is actually displayed. */
    private JTable myTable;

    /** the property */
    private final Property PROPERTY;

    /** the model. */
    private final IntrospectingTableModelInterface MODEL;

    /**
     * constructs a new ExpandButton.
     * @param property Property; the property
     * @param model IntrospectingTableModelInterface; the model
     */
    public ExpandButton(final Property property, final IntrospectingTableModelInterface model)
    {
        super("+");
        this.setMargin(new Insets(0, 0, 0, 0));
        this.PROPERTY = property;
        this.MODEL = model;

        ActionListener al = new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent e)
            {
                showTable();
            }
        };

        this.addActionListener(al);
    }

    /**
     * Sets the JTable in which this button is actually displayed. The reference is used to facilitate dialog creation.
     * @param table JTable; the table
     */
    public void setMyJTable(final JTable table)
    {
        this.myTable = table;
    }

    /**
     * Shows a new table introspecing the property.
     */
    public void showTable()
    {
        if (this.PROPERTY.getValue() == null)
        {
            return;
        }
        if (this.myTable != null)
        {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            new IntrospectionDialog(parentWindow, this.PROPERTY.getName() + ", " + this.PROPERTY.getValue(),
                    instantiateTable());
        }
        else
        {
            new IntrospectionDialog(this.PROPERTY.getName() + ", " + this.PROPERTY.getValue(), instantiateTable());
        }
    }

    /**
     * instantiates a JTable with an object model of the property.
     * @return the JTable
     */
    private JTable instantiateTable()
    {
        IntrospectingTableModelInterface newModel = null;
        ModelManager manager = this.MODEL.getModelManager();
        Introspector introspector = this.MODEL.getIntrospector();
        try
        {
            Class<?> modelClass = null;
            if (this.PROPERTY.getComposedType().isArray())
            {
                modelClass = manager.getDefaultCollectionObjectTableModel();
                Constructor<?> c = modelClass.getConstructor(new Class[] {Property.class, Introspector.class});
                newModel = (IntrospectingTableModelInterface) c.newInstance(new Object[] {this.PROPERTY, introspector});
            }
            else if (this.PROPERTY.getComposedType().isCollection())
            {
                modelClass = manager.getDefaultCollectionObjectTableModel();
                Constructor<?> c = modelClass.getConstructor(new Class[] {Property.class, Introspector.class});
                newModel = (IntrospectingTableModelInterface) c.newInstance(new Object[] {this.PROPERTY, introspector});
            }
            else if (this.PROPERTY.getComposedType().isImmutableCollection())
            {
                modelClass = manager.getDefaultCollectionObjectTableModel();
                Constructor<?> c = modelClass.getConstructor(new Class[] {Property.class, Introspector.class});
                newModel = (IntrospectingTableModelInterface) c.newInstance(new Object[] {this.PROPERTY, introspector});
            }
            else if (this.PROPERTY.getComposedType().isMap())
            {
                modelClass = manager.getDefaultMapObjectTableModel();
                Constructor<?> c = modelClass.getConstructor(new Class[] {Property.class, Introspector.class});
                newModel = (IntrospectingTableModelInterface) c.newInstance(new Object[] {this.PROPERTY, introspector});
            }
            else if (this.PROPERTY.getComposedType().isImmutableMap())
            {
                modelClass = manager.getDefaultMapObjectTableModel();
                Constructor<?> c = modelClass.getConstructor(new Class[] {Property.class, Introspector.class});
                newModel = (IntrospectingTableModelInterface) c.newInstance(new Object[] {this.PROPERTY, introspector});
            }
            else
            {
                modelClass = manager.getDefaultObjectTableModel();
                Constructor<?> c = modelClass.getConstructor(new Class[] {Object.class, Introspector.class});
                newModel =
                        (IntrospectingTableModelInterface) c.newInstance(new Object[] {this.PROPERTY.getValue(), introspector});
            }
        }
        catch (Exception exception)
        {
            CategoryLogger.always().warn(exception, "instantiate: could not instantiate parent tablemodel, using default");
            if (this.PROPERTY.getComposedType().isArray())
            {
                newModel = new CollectionTableModel(this.PROPERTY);
            }
            else if (this.PROPERTY.getComposedType().isCollection())
            {
                newModel = new CollectionTableModel(this.PROPERTY);
            }
            else if (this.PROPERTY.getComposedType().isImmutableCollection())
            {
                newModel = new ImmutableCollectionTableModel(this.PROPERTY);
            }
            else if (this.PROPERTY.getComposedType().isMap())
            {
                newModel = new MapTableModel(this.PROPERTY);
            }
            else if (this.PROPERTY.getComposedType().isImmutableMap())
            {
                newModel = new MapTableModel(this.PROPERTY);
            }
            else
            {
                newModel = new ObjectTableModel(this.PROPERTY.getValue());
            }
        }
        // Propagate CellPresentation configuration.
        CellPresentationConfiguration config = DefaultConfiguration.getDefaultConfiguration();
        if (this.myTable instanceof ICellPresentationConfigProvider)
            config = ((ICellPresentationConfigProvider) this.myTable).getCellPresentationConfiguration();
        JTable result = new ObjectJTable(newModel, config);
        // Propagate model settings
        newModel.getModelManager().setDefaultCollectionObjectTableModel(manager.getDefaultCollectionObjectTableModel());
        newModel.getModelManager().setDefaultObjectTableModel(manager.getDefaultObjectTableModel());
        result.repaint();
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "ExpandButton [PROPERTY=" + this.PROPERTY + ", MODEL=" + this.MODEL + "]";
    }
}
