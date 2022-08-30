package nl.tudelft.simulation.dsol.simulators;

import java.io.Serializable;

import org.djutils.event.EventInterface;
import org.djutils.event.EventListenerInterface;
import org.djutils.event.ref.ReferenceType;

import nl.tudelft.simulation.dsol.experiment.ReplicationInterface;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;

/**
 * The TestModel.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>,
 *         <a href="mailto:a.verbraeck@tudelft.nl">Alexander Verbraeck </a>
 */
public class TestModel extends AbstractDSOLModel<Double, SimulatorInterface<Double>> implements EventListenerInterface
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * constructs a new TestModel.
     * @param simulator the simulator
     */
    public TestModel(final SimulatorInterface<Double> simulator)
    {
        super(simulator);
    }

    /** {@inheritDoc} */
    @Override
    public void constructModel()
    {
        try
        {
            getSimulator().addListener(this, ReplicationInterface.END_REPLICATION_EVENT, ReferenceType.STRONG);
            getSimulator().addListener(this, ReplicationInterface.START_REPLICATION_EVENT, ReferenceType.STRONG);
            getSimulator().addListener(this, SimulatorInterface.START_EVENT, ReferenceType.STRONG);
            getSimulator().addListener(this, SimulatorInterface.STOP_EVENT, ReferenceType.STRONG);
            getSimulator().addListener(this, SimulatorInterface.TIME_CHANGED_EVENT, ReferenceType.STRONG);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final EventInterface event)
    {
        if (event.getType().equals(SimulatorInterface.START_EVENT))
        {
            System.out.println(getSimulator().getReplication() + " started @ t=" + getSimulator().getSimulatorTime());
        }
        if (event.getType().equals(SimulatorInterface.STOP_EVENT))
        {
            System.out.println(getSimulator().getReplication() + " stopped @ t=" + getSimulator().getSimulatorTime());
        }
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return "TestModel";
    }
}
