package nl.tudelft.simulation.dsol.formalisms.devs.esdevs;

/**
 * EOC class. EOC stands for External Output Coupling, which is a coupling between a component within a coupled model towards
 * the outside of that coupled model. The definition can be found in Zeigler et al. (2000), p. 86-87.
 * <p>
 * Copyright (c) 2009-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="http://tudelft.nl/mseck">Mamadou Seck</a><br>
 * @author <a href="http://tudelft.nl/averbraeck">Alexander Verbraeck</a><br>
 * @param <T> the time type
 * @param <P> the type of message the EOC produces.
 */
public class ExternalOutputCoupling<T extends Number & Comparable<T>, P>
{
    /** the output port of the sending component. */
    private OutputPortInterface<T, P> fromPort;

    /** the input port of the receiving component. */
    private OutputPortInterface<T, P> toPort;

    /**
     * Make the wiring between output and input.
     * @param fromPort OutputPortInterface&lt;T,P&gt;; the output port of the sending component
     * @param toPort OutputPortInterface&lt;T,P&gt;; input port of the receiving component
     * @throws Exception in case of wiring to self
     */
    public ExternalOutputCoupling(final OutputPortInterface<T, P> fromPort, final OutputPortInterface<T, P> toPort) throws Exception
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
    public OutputPortInterface<T, P> getToPort()
    {
        return this.toPort;
    }
}
