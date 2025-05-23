package nl.tudelft.simulation.dsol.swing.introspection.sortable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * The sortingTableHeader class.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>.
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>.
 * @author Niels Lang.
 * @since 1.5
 */
public class SortingTableHeader extends JTableHeader
{
    /** */
    private static final long serialVersionUID = 20140831L;

    /** the definitions of the tableHeader. */
    protected List<Sortable.Definition> definitions = new ArrayList<Sortable.Definition>(5);

    /**
     * constructs a new SortingTableHeader.
     * @param defaultSorting Sortable.Definition[]; the defaultSorting definitions
     */
    public SortingTableHeader(final Sortable.Definition[] defaultSorting)
    {
        this.definitions.addAll(Arrays.asList(defaultSorting));
        this.init();
    }

    /**
     * initializes the SortingTableHeader.
     */
    private void init()
    {
        this.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(final MouseEvent e)
            {
                int column = columnAtPoint(e.getPoint());
                TableColumn col = getColumnModel().getColumn(column);
                SortingHeaderCell header = (SortingHeaderCell) col.getHeaderRenderer();
                header.changeSort();
                if (!e.isShiftDown())
                {
                    SortingTableHeader.this.definitions.clear();
                    SortingTableHeader.this.clearHeaders(header);
                    if (header.isSorted())
                        SortingTableHeader.this.definitions.add(new SortDefinition(column, header.isAscendingSorted()));
                }
                else
                {
                    Sortable.Definition def = SortingTableHeader.this.getMatchingDef(column);
                    if (def == null)
                        SortingTableHeader.this.definitions.add(new SortDefinition(column, header.isAscendingSorted()));
                    else
                    {
                        if (!header.isSorted())
                            SortingTableHeader.this.definitions.remove(def);
                        else
                            def.setAscending(header.isAscendingSorted());
                    }
                }
                SortingTableHeader.this.propagateSort();
            }

        });
    }

    /**
     * gets the matching definition.
     * @param column the columnNumber
     * @return the definition
     */
    protected Sortable.Definition getMatchingDef(final int column)
    {
        for (Sortable.Definition def : this.definitions)
        {
            if (def.getFieldID() == column)
            { return def; }
        }
        return null;
    }

    /**
     * clears the header.
     * @param butThisOne all but this one
     */
    protected void clearHeaders(final SortingHeaderCell butThisOne)
    {
        for (int i = 0; i < getColumnModel().getColumnCount(); i++)
        {
            SortingHeaderCell current = (SortingHeaderCell) getColumnModel().getColumn(i).getHeaderRenderer();
            if (!current.equals(butThisOne))
            { current.setSort(SortingHeaderCell.SORT_NONE); }
        }
    }

    /**
     * propagates the sort.
     */
    protected void propagateSort()
    {
        TableModel model = this.getTable().getModel();
        if (model instanceof Sortable)
        {
            ((Sortable) model).setDefinitions(this.definitions.toArray(new SortDefinition[0]));
            ((Sortable) model).sort();
        }
    }

    @Override
    public void setColumnModel(final TableColumnModel columnModel)
    {
        super.setColumnModel(columnModel);
        for (int i = 0; i < this.getColumnModel().getColumnCount(); i++)
        {
            int sort = getSort(i);
            this.getColumnModel().getColumn(i).setHeaderRenderer(new SortingHeaderCell(sort));
        }
    }

    /**
     * gets the sorting of the column.
     * @param column the column number
     * @return the value
     */
    private int getSort(final int column)
    {
        for (Sortable.Definition def : this.definitions)
        {
            if (def.getFieldID() == column)
            {
                if (def.isAcendingSort())
                { return SortingHeaderCell.SORT_ASCENDING; }
                return SortingHeaderCell.SORT_DESCENDING;
            }
        }
        return SortingHeaderCell.SORT_NONE;
    }

    @Override
    public void setTable(final JTable table)
    {
        super.setTable(table);
        this.propagateSort();
    }
}
