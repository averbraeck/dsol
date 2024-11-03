package nl.tudelft.simulation.dsol.eventlists;

import java.util.Collection;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.djutils.event.EventType;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;

/**
 * A TableModel implementation of an eventlist is an extionsion of the eventlist which upholds its own TableModel. This
 * implementation is used to graphically display the events in the tree.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @param <T> the type of simulation time, e.g. SimTimeCalendarLong or Double or DoubleUnit.
 * @since 1.5
 */
public class TableModelEventList<T extends Number & Comparable<T>> extends RedBlackTree<T>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** The EVENTLIST_CHANGED_EVENT. */
    public static final EventType EVENTLIST_CHANCED_EVENT = null;

    /** The tableHeader. */
    public static final String[] HEADER = {"Time", "Target", "Method"};

    /** the tableModel. */
    private DefaultTableModel tableModel = new DefaultTableModel(HEADER, 0);

    /** show the package information in the tableModel. */
    private boolean showPackage = false;

    /**
     * constructs a new TableModelEventList.
     * @param origin EventListInterface&lt;T&gt;; the origin
     */
    public TableModelEventList(final EventListInterface<T> origin)
    {
        synchronized (origin)
        {
            for (SimEventInterface<T> event : origin)
            {
                add(event);
            }
        }
    }

    /**
     * returns the tableModel.
     * @return TableModel result
     */
    public TableModel getTableModel()
    {
        return this.tableModel;
    }

    /**
     * update the tableModel.
     */
    private void updateTableModel()
    {
        try
        {
            this.tableModel.setRowCount(0);
            for (SimEventInterface<T> simEventInterface : this)
            {
                if (simEventInterface instanceof SimEvent)
                {
                    Object[] row = new Object[3];
                    SimEvent<T> event = (SimEvent<T>) simEventInterface;
                    row[0] = event.getAbsoluteExecutionTime().toString();
                    row[1] = this.formatObject(event.getTarget().toString());
                    row[2] = this.formatObject(event.getMethod().toString());
                    this.tableModel.addRow(row);
                }
            }

        }
        catch (Exception exception)
        {
            CategoryLogger.always().warn(exception, "updateTableModel");
        }
    }

    /**
     * formats a label representing an object.
     * @param label String; the label to format.
     * @return String the label
     */
    private String formatObject(final String label)
    {
        if (label == null)
        {
            return "null";
        }
        if (this.showPackage)
        {
            return label;
        }
        return label.substring(label.lastIndexOf(".") + 1);
    }

    /**
     * sets the showPackage.
     * @param showPackage boolean; The showPackage to set.
     */
    public void setShowPackage(final boolean showPackage)
    {
        this.showPackage = showPackage;
    }

    /**
     * Add all events in the collection to the ventlist.
     * @param collection events
     */
    public synchronized void addAll(final Collection<? extends SimEventInterface<T>> collection)
    {
        synchronized (this.tableModel)
        {
            for (SimEventInterface<T> event : collection)
            {
                add(event);
            }
            this.updateTableModel();
        }
    }

    @Override
    public synchronized void clear()
    {
        synchronized (this.tableModel)
        {
            super.clear();
            this.updateTableModel();
        }
    }

    @Override
    public synchronized SimEventInterface<T> removeFirst()
    {
        synchronized (this.tableModel)
        {
            SimEventInterface<T> result = super.removeFirst();
            this.updateTableModel();
            return result;
        }
    }

}
