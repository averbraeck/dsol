package nl.tudelft.simulation.examples.dsol.mm1queue;

import java.io.Serializable;

import nl.tudelft.simulation.dsol.formalisms.Resource;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * The Seize is an extended Seize block whic sets delay times on arriving customers..
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="http://www.tbm.tudelft.nl/webstaf/peterja/index.htm">Peter Jacobs </a>
 */
public class Seize extends nl.tudelft.simulation.dsol.formalisms.flow.Seize<Double>
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * constructs a new Seize.
     * @param id the id of the Release block
     * @param simulator DevsSimulatorInterface&lt;Double&gt;; the devs simulator on which to schedule
     * @param resource Resource&lt;Double,Double,Double&gt;; the resource to claim
     */
    public Seize(final Serializable id, final DevsSimulatorInterface<Double> simulator,
            final Resource<Double> resource)
    {
        super(id, simulator, resource);
    }

    /**
     * constructs a new Seize.
     * @param id the id of the Release block
     * @param simulator DevsSimulatorInterface&lt;Double&gt;; the devs simulator on which to schedule
     * @param resource Resource&lt;Double,Double,Double&gt;; the resource to claim
     * @param requestedCapacity double; the amount to claim
     */
    public Seize(final Serializable id, final DevsSimulatorInterface<Double> simulator,
            final Resource<Double> resource, final double requestedCapacity)
    {
        super(id, simulator, resource, requestedCapacity);
    }

    /** {@inheritDoc} */
    @Override
    public void receiveObject(final Object object)
    {
        Customer customer = (Customer) object;
        customer.setEntranceTime(this.simulator.getSimulatorTime());
        super.receiveObject(object);
    }
}
