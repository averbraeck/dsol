package nl.tudelft.simulation.dsol.statistics.table;

import java.rmi.RemoteException;

import javax.swing.table.DefaultTableModel;

import org.djutils.event.EventListener;
import org.djutils.event.EventProducer;
import org.djutils.event.EventType;

/**
 * The StatisticsTableModel class defines the tableModel used to represent statistics objects as a table, e.g., on the screen or
 * on a web page. Although the TableModel is defined in swing, it is independent of its representation on the screen. The actual
 * representation of the table on the screen or on a web page can be found in the dsol-swing and dsol-web projects.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck" target="_blank"> Alexander Verbraeck</a>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public abstract class StatisticsTableModel extends DefaultTableModel implements EventListener
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * constructs a new StatisticsTableModel.
     * @param columnNames Object[]; the names of the columns
     * @param rows int; the number of rows
     * @param dataProducer EventProducer; the statistic that produces the updates to the table
     * @param eventTypes EventType[]; the eventTypes after which the StatisticsTable should update its content
     * @throws RemoteException when there is a problem communicating with the data producing table
     */
    public StatisticsTableModel(final Object[] columnNames, final int rows, final EventProducer dataProducer,
            final EventType[] eventTypes) throws RemoteException
    {
        super(columnNames, rows);
        for (EventType eventType : eventTypes)
        {
            dataProducer.addListener(this, eventType);
        }
    }

}
