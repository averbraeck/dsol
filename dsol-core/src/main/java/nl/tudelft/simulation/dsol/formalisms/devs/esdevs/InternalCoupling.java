package nl.tudelft.simulation.dsol.formalisms.devs.esdevs;

/**
 * IC class. IC stands for Internal Coupling, which is a coupling between two components within a coupled model. The definition
 * can be found in Zeigler et al. (2000), p. 86-87.
 * <p>
 * Copyright (c) 2009-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="http://tudelft.nl/mseck">Mamadou Seck</a><br>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a><br>
 * @param <T> the time type
 * @param <P> the type of message the IC can transfer.
 */
public class InternalCoupling<T extends Number & Comparable<T>, P>
{
    /** the output port of the sending component. */
    private OutputPortInterface<T, P> fromPort;

    /** the input port of the receiving component. */
    private InputPortInterface<T, P> toPort;

    /**
     * Make the wiring between output and input.
     * @param fromPort OutputPortInterface&lt;T,P&gt;; the output port of the sending component
     * @param toPort InputPortInterface&lt;T,P&gt;; input port of the receiving component
     * @throws Exception in case of wiring to self
     */
    public InternalCoupling(final OutputPortInterface<T, P> fromPort, final InputPortInterface<T, P> toPort) throws Exception
    {
        this.fromPort = fromPort;
        this.toPort = toPort;

        if (this.fromPort.getModel().equals(toPort.getModel()))
        {
            throw new Exception("no self coupling allowed");
        }

    }

    /**
     * @return the output port of the sending component.
     */
    public OutputPortInterface<T, P> getFromPort()
    {
        return this.fromPort;
    }

    /**
     * @return the input port of the receiving component.
     */
    public InputPortInterface<T, P> getToPort()
    {
        return this.toPort;
    }
}
