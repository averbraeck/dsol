package nl.tudelft.simulation.dsol.statistics.table;

import java.rmi.RemoteException;

import org.djutils.event.EventInterface;
import org.djutils.event.TimedEventType;

import nl.tudelft.simulation.dsol.statistics.SimPersistent;

/**
 * PersistentTableModel maintains a table with all statistics data from the SimPersistent.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://opentrafficsim.org/docs/current/license.html">OpenTrafficSim License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 * @author <a href="https://www.transport.citg.tudelft.nl">Wouter Schakel</a>
 */
public class PersistentTableModel extends StatisticsTableModel
{
    /** */
    private static final long serialVersionUID = 20200307L;

    /** the column names. */
    private static final String[] COLUMN_NAMES = {"field", "value"};

    /** the persistent. */
    private SimPersistent<?> persistent;

    /**
     * Constructor.
     * @param persistent SimPersistent; the persistent for which the table is created
     * @throws RemoteException when communication with the persistent fails
     */
    public PersistentTableModel(final SimPersistent<?> persistent) throws RemoteException
    {
        super(COLUMN_NAMES, 8, persistent,
                new TimedEventType[] {SimPersistent.TIMED_INITIALIZED_EVENT, SimPersistent.TIMED_OBSERVATION_ADDED_EVENT});
        this.persistent = persistent;
        notify(null);
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final EventInterface event) throws RemoteException
    {
        setValueAt("name", 0, 0);
        setValueAt("n", 1, 0);
        setValueAt("min value", 2, 0);
        setValueAt("max value", 3, 0);
        setValueAt("weighted sample-mean", 4, 0);
        setValueAt("weighted sample-variance", 5, 0);
        setValueAt("weighted sample-st.dev.", 6, 0);
        setValueAt("weighted sum", 7, 0);

        // Since the result is subscribed to the actual values
        // there is no need to create a synchronized block.
        setValueAt(this.persistent.getDescription(), 0, 1);
        setValueAt(Long.valueOf(this.persistent.getN()), 1, 1);
        setValueAt(Double.valueOf(this.persistent.getMin()), 2, 1);
        setValueAt(Double.valueOf(this.persistent.getMax()), 3, 1);
        setValueAt(Double.valueOf(this.persistent.getWeightedSampleMean()), 4, 1);
        setValueAt(Double.valueOf(this.persistent.getWeightedSampleVariance()), 5, 1);
        setValueAt(Double.valueOf(this.persistent.getWeightedSampleStDev()), 6, 1);
        setValueAt(Double.valueOf(this.persistent.getWeightedSum()), 7, 1);
    }

}
