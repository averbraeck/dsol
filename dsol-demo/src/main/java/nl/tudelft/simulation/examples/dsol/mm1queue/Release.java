package nl.tudelft.simulation.examples.dsol.mm1queue;

import java.io.Serializable;

import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

import nl.tudelft.simulation.dsol.formalisms.Resource;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;

/**
 * The Seize is an extended Seize block which computes the servicetime. <br>
 * Copyright (c) 2003-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology </a>, the Netherlands. <br>
 * See for project information <a href="https://simulation.tudelft.nl"> www.simulation.tudelft.nl </a> <br>
 * License of use: <a href="http://www.gnu.org/copyleft/gpl.html">General Public License (GPL) </a>, no warranty <br>
 * @author <a href="http://www.tbm.tudelft.nl/webstaf/peterja/index.htm">Peter Jacobs </a>
 */
public class Release extends nl.tudelft.simulation.dsol.formalisms.flow.Release<Double>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** SERVICE_TIME_EVENT is fired when a customer is released. */
    public static final EventType SERVICE_TIME_EVENT = new EventType(new MetaData("SERVICE_TIME_EVENT",
            "Service Time observation", new ObjectDescriptor("serviceTime", "Service time", Double.class)));

    /**
     * constructs a new Release.
     * @param id the id of the Release block
     * @param simulator DEVSSimulatorInterface&lt;Double&gt;; the simulator on which to schedule
     * @param resource Resource&lt;Double,Double,Double&gt;; the resource to be released
     */
    public Release(final Serializable id, final DEVSSimulatorInterface<Double> simulator,
            final Resource<Double> resource)
    {
        super(id, simulator, resource);
    }

    /**
     * constructs a new Release.
     * @param id the id of the Release block
     * @param simulator DEVSSimulatorInterface&lt;Double&gt;; the simulator on which to schedule
     * @param resource Resource&lt;Double,Double,Double&gt;; the resource to be released
     * @param amount double; the amount to be released
     */
    public Release(final Serializable id, final DEVSSimulatorInterface<Double> simulator,
            final Resource<Double> resource, final double amount)
    {
        super(id, simulator, resource, amount);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void releaseObject(final Object object)
    {
        if (object instanceof Customer)
        {
            Customer customer = (Customer) object;
            double serviceTime = this.simulator.getSimulatorTime() - customer.getEntranceTime();
            this.fireTimedEvent(Release.SERVICE_TIME_EVENT, serviceTime, this.simulator.getSimulatorTime());
            super.releaseObject(object);
        }
    }
}
