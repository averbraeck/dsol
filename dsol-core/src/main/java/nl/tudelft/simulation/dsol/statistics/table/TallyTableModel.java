package nl.tudelft.simulation.dsol.statistics.table;

import java.rmi.RemoteException;

import org.djutils.event.Event;
import org.djutils.event.EventType;

import nl.tudelft.simulation.dsol.statistics.SimTally;

/**
 * TallyTableModel maintains a table with all statistics data from the SimTally.
 * <p>
 * Copyright (c) 2020- 2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://opentrafficsim.org/docs/current/license.html">OpenTrafficSim License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 * @author <a href="https://www.transport.citg.tudelft.nl">Wouter Schakel</a>
 */
public class TallyTableModel extends StatisticsTableModel
{
    /** */
    private static final long serialVersionUID = 20200307L;

    /** the column names. */
    private static final String[] COLUMN_NAMES = {"field", "value"};

    /** the tally. */
    private SimTally<?> tally;

    /**
     * Constructor.
     * @param tally SimTally; the tally for which the table is created
     * @throws RemoteException when communication with the tally fails
     */
    public TallyTableModel(final SimTally<?> tally) throws RemoteException
    {
        super(COLUMN_NAMES, 10, tally,
                new EventType[] {SimTally.TIMED_INITIALIZED_EVENT, SimTally.TIMED_OBSERVATION_ADDED_EVENT});
        this.tally = tally;
        notify(null);
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final Event event) throws RemoteException
    {
        setValueAt("name", 0, 0);
        setValueAt("n", 1, 0);
        setValueAt("min", 2, 0);
        setValueAt("max", 3, 0);
        setValueAt("sample-mean", 4, 0);
        setValueAt("sample-variance", 5, 0);
        setValueAt("sample-st.dev.", 6, 0);
        setValueAt("sample-skewness", 7, 0);
        setValueAt("sample-kurtosis", 8, 0);
        setValueAt("sum", 9, 0);

        // Since the result is subscribed to the actual values
        // there is no need to create a synchronized block.
        setValueAt(this.tally.getDescription(), 0, 1);
        setValueAt(Long.valueOf(this.tally.getN()), 1, 1);
        setValueAt(Double.valueOf(this.tally.getMin()), 2, 1);
        setValueAt(Double.valueOf(this.tally.getMax()), 3, 1);
        setValueAt(Double.valueOf(this.tally.getSampleMean()), 4, 1);
        setValueAt(Double.valueOf(this.tally.getSampleVariance()), 5, 1);
        setValueAt(Double.valueOf(this.tally.getSampleStDev()), 6, 1);
        setValueAt(Double.valueOf(this.tally.getSampleSkewness()), 7, 1);
        setValueAt(Double.valueOf(this.tally.getSampleKurtosis()), 8, 1);
        setValueAt(Double.valueOf(this.tally.getSum()), 9, 1);
    }

}
