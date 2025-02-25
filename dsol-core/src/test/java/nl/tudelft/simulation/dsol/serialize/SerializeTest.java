package nl.tudelft.simulation.dsol.serialize;

import java.io.IOException;
import java.rmi.MarshalledObject;

import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.dsol.eventlists.RedBlackTree;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.dsol.simulators.DessSimulator;
import nl.tudelft.simulation.dsol.simulators.DevDessAnimator;
import nl.tudelft.simulation.dsol.simulators.DevDessSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsRealTimeAnimator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.jstats.ode.integrators.NumericalIntegratorType;

/**
 * This class defines the JUnit test for the SerializeTest.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>,
 *         <a href="mailto:a.verbraeck@tudelft.nl">Alexander Verbraeck </a>
 */
public class SerializeTest
{

    /**
     * tests the serializability of several simulator objects.
     * @throws IOException on marshalling error
     */
    @Test
    public void testSerializability() throws IOException
    {
        // We start with the simulators.
        new MarshalledObject(new DevsSimulator("SerializeTest"));
        new MarshalledObject(new DessSimulator("SerializeTest", 0.1));
        new MarshalledObject(new DevDessSimulator("SerializeTest", 0.1));
        new MarshalledObject(new DevDessAnimator("SerializeTest", 0.1));
        new MarshalledObject(new DevsRealTimeAnimator.TimeDoubleUnit("SerializeTest"));

        // ---------- Let's test the formalisms ----------------

        // The DEVS formalism
        new MarshalledObject(new RedBlackTree());
        new MarshalledObject(new SimEvent(new Double(1.1), "Peter", "toString", null));

        // The DESS formalism
        new MarshalledObject(
                new SimpleDifferentialEquation(new DessSimulator("SerializeTest", 0.1), 0.1, NumericalIntegratorType.ADAMS, 1));

        // The process interaction formalism
        // XXX: gives error; first check interpreter package: new Process(new DevsSimulator());

    }
}
