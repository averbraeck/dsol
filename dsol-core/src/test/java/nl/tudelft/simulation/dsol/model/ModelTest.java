package nl.tudelft.simulation.dsol.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.Test;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.StreamInformation;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterDouble;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * ModelTest.java.
 * <p>
 * Copyright (c) 2021-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class ModelTest
{

    /**
     * Test the AbstractDsolModel class.
     * @throws InputParameterException on error with input parameter
     */
    @Test
    public void testDsolModel() throws InputParameterException
    {
        DevsSimulatorInterface<Double> simulator = new DevsSimulator<Double>("sim");
        DsolModel<Double, SimulatorInterface<Double>> model =
                new AbstractDsolModel<Double, SimulatorInterface<Double>>(simulator)
                {
                    @Override
                    public void constructModel() throws SimRuntimeException
                    {
                        //
                    }
                };
        assertEquals(simulator, model.getSimulator());

        // stream management
        assertNotNull(model.getStream("default"));
        assertNotNull(model.getDefaultStream());
        assertEquals(model.getDefaultStream(), model.getStream("default"));
        StreamInterface stream = model.getDefaultStream();
        double rand = stream.nextDouble();
        stream.nextDouble();
        stream.nextDouble();
        model.resetStreams();
        assertEquals(rand, stream.nextDouble(), 1E-9);
        Map<String, StreamInterface> streamMap = model.getStreams();
        streamMap.put("default", new MersenneTwister(20L));
        streamMap.put("stream1", new MersenneTwister(1L));
        assertNotNull(model.getStream("stream1"));

        // initial streams
        StreamInformation streamInfo = new StreamInformation();
        streamInfo.addStream("default", new MersenneTwister(20L));
        streamInfo.addStream("extra", new MersenneTwister(10L));
        model.setStreamInformation(streamInfo);
        assertEquals(2, model.getStreams().size());
        assertEquals(20, model.getStream("default").getSeed());
        assertEquals(10, model.getStream("extra").getSeed());

        // input parameter management
        assertNotNull(model.getInputParameterMap());
        model.getInputParameterMap().add(new InputParameterDouble("double", "double", "double", 10.0, 1.0));
        assertEquals(10.0, (double) model.getInputParameterMap().get("double").getDefaultValue(), 1E-6);
    }

}
