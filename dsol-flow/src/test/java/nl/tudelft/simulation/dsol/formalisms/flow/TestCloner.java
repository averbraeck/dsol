package nl.tudelft.simulation.dsol.formalisms.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;

import com.rits.cloning.Cloner;

import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * TestClone.java.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TestCloner
{
    /** the cloner to clone entities. */
    private static final Cloner cloner = Cloner.standard();

    static
    {
        cloner.dontCloneInstanceOf(StreamInterface.class, SimulatorInterface.class, AbstractDsolModel.class, Replication.class);
    }

    /**
     * @param args not used
     */
    public static void main(final String[] args)
    {
        StreamInterface stream = new MersenneTwister(12L);
        DistContinuous dist1 = new DistExponential(stream, 2.0);
        System.out.println(dist1 + "  " + System.identityHashCode(dist1) + "  " + System.identityHashCode(dist1.getStream()));
        DistContinuous dist2 = cloner.deepClone(dist1);
        System.out.println(dist2 + "  " + System.identityHashCode(dist2) + "  " + System.identityHashCode(dist2.getStream()));
        System.out.println(dist1.draw());
        System.out.println(dist2.draw() + "\n");

        var list1 = new ArrayList<DistContinuous>();
        list1.add(dist1);
        list1.add(dist1);
        var list2 = cloner.deepClone(list1);
        for (int i = 0; i < 2; i++)
            System.out.println(list2.get(i) + "  " + System.identityHashCode(list2.get(i)) + "  "
                    + System.identityHashCode(list2.get(i).getStream()));
        assertNotEquals(dist1, list2.get(0));
        assertEquals(dist1.getStream(), list2.get(0).getStream());
    }

}
