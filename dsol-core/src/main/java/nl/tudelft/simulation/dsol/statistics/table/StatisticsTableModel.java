package nl.tudelft.simulation.dsol.statistics.table;

import java.rmi.RemoteException;

import javax.swing.table.DefaultTableModel;

import org.djutils.event.EventListenerInterface;
import org.djutils.event.EventProducerInterface;
import org.djutils.event.EventTypeInterface;

/**
 * The StatisticsTableModel class defines the tableModel used to represent statistics objects as a table, e.g., on the screen or
 * on a web page. Although the TableModel is defined in swing, it is independent of its representation on the screen. The actual
 * representation of the table on the screen or on a web page can be found in the dsol-swing and dsol-web projects.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public abstract class StatisticsTableModel extends DefaultTableModel implements EventListenerInterface
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * constructs a new StatisticsTableModel.
     * @param columnNames Object[]; the names of the columns
     * @param rows int; the number of rows
     * @param dataProducer EventProducerInterface; the statistic that produces the updates to the table
     * @param eventTypes EventType[]; the eventTypes after which the StatisticsTable should update its content
     * @throws RemoteException when there is a problem communicating with the data producing table
     */
    public StatisticsTableModel(final Object[] columnNames, final int rows, final EventProducerInterface dataProducer,
            final EventTypeInterface[] eventTypes) throws RemoteException
    {
        super(columnNames, rows);
        for (EventTypeInterface eventType : eventTypes)
        {
            dataProducer.addListener(this, eventType);
        }
    }

}
