package nl.tudelft.simulation.dsol.swing.introspection.gui;

import java.awt.BorderLayout;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.Collection;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.djutils.immutablecollections.ImmutableCollection;
import org.djutils.immutablecollections.ImmutableMap;

import nl.tudelft.simulation.dsol.swing.introspection.mapping.CellPresentationConfiguration;
import nl.tudelft.simulation.dsol.swing.introspection.mapping.DefaultConfiguration;
import nl.tudelft.simulation.dsol.swing.introspection.sortable.SortDefinition;
import nl.tudelft.simulation.dsol.swing.introspection.sortable.SortingTableHeader;

/**
 * * A customization of a standard JTable to allow the display of an introspected object. The behaviour of the ObjectJTable
 * depends on the contained TableModel. @see ObjectTableModel provides a view of the properties and values of a single
 * introspected object. @see CollectionTableModel provides a view on a collection of instances: usually the value of a composite
 * property.
 * <p>
 * A configuration mechanism is implemented to load the editors and renders to be used by this JTable. See @see
 * #setConfig(nl.tudelft.simulation.introspection.mapping.CellPresentationConfiguration) for details.
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
public class ObjectJTable extends JTable implements ObjectJTableInterface, ICellPresentationConfigProvider
{
    /** */
    private static final long serialVersionUID = 20140831L;

    /** the updateTimer. */
    private static UpdateTimer updateTimer = new UpdateTimer(100L);

    /** hasShown? */
    protected boolean hasShown = false;

    /** the introspectionTableModel. */
    private IntrospectingTableModelInterface introspectionTableModel;

    /**
     * The configuration used to assign renderers and editors to cells.
     */
    private final CellPresentationConfiguration CONFIG;

    /**
     * constructs a new ObjectJTable.
     * @param dm IntrospectingTableModelInterface; the defaultTableModel
     */
    public ObjectJTable(final IntrospectingTableModelInterface dm)
    {
        this(dm, DefaultConfiguration.getDefaultConfiguration());
    }

    /**
     * constructs a new ObjectJTable.
     * @param dm IntrospectingTableModelInterface; the defaultTableModel
     * @param config CellPresentationConfiguration; the CellPresentationConfiguration
     */
    public ObjectJTable(final IntrospectingTableModelInterface dm, final CellPresentationConfiguration config)
    {
        super(new SortingObjectTableModel(dm));
        this.CONFIG = config;
        init(dm);
    }

    /**
     * Constructor for ObjectJTable.
     * @param dm IntrospectingTableModelInterface; the defaultTableModel
     * @param cm TableColumnModel; the tableColumnModel
     */
    public ObjectJTable(final IntrospectingTableModelInterface dm, final TableColumnModel cm)
    {
        super(new SortingObjectTableModel(dm), cm);
        this.CONFIG = DefaultConfiguration.getDefaultConfiguration();
        init(dm);
    }

    /**
     * Constructor for ObjectJTable.
     * @param dm IntrospectingTableModelInterface; the defaultTableModel
     * @param cm TableColumnModel; the tableColumnModel
     * @param sm ListSelectionModel; the listSelectionModel
     */
    public ObjectJTable(final IntrospectingTableModelInterface dm, final TableColumnModel cm, final ListSelectionModel sm)
    {
        super(new SortingObjectTableModel(dm), cm, sm);
        this.CONFIG = DefaultConfiguration.getDefaultConfiguration();
        init(dm);
    }

    /** {@inheritDoc} */
    @Override
    public CellPresentationConfiguration getCellPresentationConfiguration()
    {
        return this.CONFIG;
    }

    /**
     * initializes the objectJTable.
     * @param model IntrospectingTableModelInterface; the model
     */
    private void init(IntrospectingTableModelInterface model)
    {
        this.introspectionTableModel = model;
        initConfig();
        setPreferredScrollableViewportSize(this.getPreferredSize());
        JTableHeader header = new SortingTableHeader(new SortDefinition[] {new SortDefinition(0, true)});
        this.setTableHeader(header);
        header.setColumnModel(this.getColumnModel());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(header, BorderLayout.NORTH);
        panel.add(this, BorderLayout.CENTER);

        ObjectJTable.updateTimer.add(this);
    }

    /**
     * Enables the installation of a special renderer for arrays and Collections.
     * @see javax.swing.JTable#getDefaultRenderer(java.lang.Class)
     */
    @Override
    public TableCellRenderer getDefaultRenderer(Class<?> columnClass)
    {
        if (columnClass.isArray())
            return super.getDefaultRenderer(Object[].class);
        if (Collection.class.isAssignableFrom(columnClass))
            return super.getDefaultRenderer(Collection.class);
        if (ImmutableCollection.class.isAssignableFrom(columnClass))
            return super.getDefaultRenderer(ImmutableCollection.class);
        if (Map.class.isAssignableFrom(columnClass))
            return super.getDefaultRenderer(Map.class);
        if (ImmutableMap.class.isAssignableFrom(columnClass))
            return super.getDefaultRenderer(ImmutableMap.class);
        return super.getDefaultRenderer(columnClass);
    }

    /**
     * the ParentListener.
     */
    protected class ParentListener implements HierarchyListener
    {
        /** {@inheritDoc} */
        @Override
        public void hierarchyChanged(final HierarchyEvent e)
        {
            if (e.getChangeFlags() == HierarchyEvent.DISPLAYABILITY_CHANGED)
            {
                if (!ObjectJTable.this.hasShown && isDisplayable())
                {
                    ObjectJTable.this.hasShown = true;
                    return;
                }
                if (ObjectJTable.this.hasShown && !isDisplayable())
                {
                    ObjectJTable.this.getModel().removeTableModelListener(ObjectJTable.this);
                }
            }
        }
    }

    /**
     * Initializes the configuration, by propagating its settings to the table's set of default renderers/editors.
     */
    private void initConfig()
    {
        addHierarchyListener(new ParentListener());
        Class<?>[][] renderers = this.CONFIG.getRenderers();
        Class<?>[][] editors = this.CONFIG.getEditors();
        try
        {
            for (int i = 0; i < renderers.length; i++)
            {
                this.setDefaultRenderer(renderers[i][0], (TableCellRenderer) renderers[i][1].newInstance());
            }
            for (int i = 0; i < editors.length; i++)
            {
                this.setDefaultEditor(editors[i][0], (TableCellEditor) editors[i][1].newInstance());
            }
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Configuration " + this.CONFIG + "failed, " + "probably invalid classes.");
        }
        this.getColumn(getColumnName(0)).setPreferredWidth(150);
        this.getColumn(getColumnName(1)).setMaxWidth(25);
        if (this.getColumnCount() == 3)
        {
            this.getColumn(getColumnName(2)).setPreferredWidth(600);
        }
        else
        {
            this.getColumn(getColumnName(2)).setPreferredWidth(200);
            this.getColumn(getColumnName(3)).setPreferredWidth(400);
        }
        this.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    }

    /** {@inheritDoc} */
    @Override
    public IntrospectingTableModelInterface getIntrospectingTableModel()
    {
        return this.introspectionTableModel;
    }
}
