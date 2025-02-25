package nl.tudelft.simulation.dsol.simulators;

/**
 * A TestExperimentalFrame.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>,
 *         <a href="mailto:a.verbraeck@tudelft.nl">Alexander Verbraeck </a>
 */
public final class ExperimentUtilDouble
{
    /**
     * STARTTIME defines the starting time for the experiment in millisec since 1970.
     */
    public static final long STARTTIME = 0;

    /** RUNLENGTH is the runLength for this experiment. */
    public static final double RUNLENGTH = 100;

    /** WARMUP period defines the warmup period for the experiment. */
    public static final double WARMUP = 10;

    /** SEED is the seed value for the DEFAULT stream. */
    public static final long SEED = 42;

    /** TIMESTEP is the timeStep to be used for the DESS formalism. */
    public static final double TIMESTEP = 0.01;

    /**
     * constructs a new TestExperimentalFrame.
     */
    private ExperimentUtilDouble()
    {
        // unreachable code
    }

    /**
     * creates a new TestExperimentalFrame.
     * @return ExperimentalFrame
     * @throws NamingException on error
     */
//    public static Experiment<Double, SimulatorInterface<Double>> createExperiment() throws NamingException
//    {
//        Experiment<Double, SimulatorInterface<Double>> experiment = new Experiment<Double>();
//        experiment.setTreatment(ExperimentUtilDouble.createTreatment(experiment));
//        experiment.setReplications(ExperimentUtilDouble.createReplications(experiment));
//        return experiment;
//    }

}
