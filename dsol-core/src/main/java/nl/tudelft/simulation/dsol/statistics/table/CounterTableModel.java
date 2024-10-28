package nl.tudelft.simulation.dsol.statistics.table;

import java.rmi.RemoteException;

import org.djutils.event.Event;
import org.djutils.event.EventType;

import nl.tudelft.simulation.dsol.statistics.SimCounter;

/**
 * CounterTableModel maintains a table with all statistics data from the SimCounter.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://opentrafficsim.org/docs/current/license.html">OpenTrafficSim License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 * @author <a href="https://www.transport.citg.tudelft.nl">Wouter Schakel</a>
 */
public class CounterTableModel extends StatisticsTableModel
{
    /** */
    private static final long serialVersionUID = 20200307L;

    /** the column names. */
    private static final String[] COLUMN_NAMES = {"field", "value"};

    /** the counter. */
    private SimCounter<?> counter;

    /**
     * Constructor.
     * @param counter SimCounter; the counter for which the table is created
     * @throws RemoteException when communication with the counter fails
     */
    public CounterTableModel(final SimCounter<?> counter) throws RemoteException
    {
        super(COLUMN_NAMES, 3, counter,
                new EventType[] {SimCounter.TIMED_INITIALIZED_EVENT, SimCounter.TIMED_OBSERVATION_ADDED_EVENT});
        this.counter = counter;
        notify(null);
    }

    @Override
    public void notify(final Event event) throws RemoteException
    {
        setValueAt("name", 0, 0);
        setValueAt("n", 1, 0);
        setValueAt("count", 2, 0);
        setValueAt(this.counter.getDescription(), 0, 1);
        setValueAt(Long.valueOf(this.counter.getN()), 1, 1);
        setValueAt(Long.valueOf(this.counter.getCount()), 2, 1);
    }

}
