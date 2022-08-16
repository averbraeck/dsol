package nl.tudelft.simulation.dsol.serialize;

import java.io.Serializable;

import nl.tudelft.simulation.dsol.formalisms.process.Process;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;

/**
 * The histogram specifies a histogram chart for the DSOL framework.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs"> Peter Jacobs </a>
 * @since 1.5
 */
public class SimpleProcess extends Process
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * constructs a new Process.
     * @param simulator the simulator
     */
    public SimpleProcess(final DEVSSimulatorInterface simulator)
    {
        super(simulator);
    }

    /** {@inheritDoc} */
    @Override
    public void process()
    {
        // we do not specify the process
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return "Process";
    }

}
