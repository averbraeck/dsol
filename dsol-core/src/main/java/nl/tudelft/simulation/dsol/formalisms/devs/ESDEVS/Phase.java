package nl.tudelft.simulation.dsol.formalisms.devs.ESDEVS;

import java.io.Serializable;

/**
 * Phase class. The Phase is used in phase explicit DEVS models. Phases partition the state space into regions, where events are
 * used to transfer the state from one phase to another phase. An example is a machine that can be in three different phases:
 * idle, active, or failed. There are explicit events to transfer the model from one phase to another. A model or component is
 * always in exactly one phase, and a transition diagram can be used to depict the transitions between phases.
 * <p>
 * Copyright (c) 2009-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="http://tudelft.nl/mseck">Mamadou Seck</a><br>
 * @author <a href="http://tudelft.nl/averbraeck">Alexander Verbraeck</a><br>
 */
public class Phase implements Serializable
{
    /** the default serial version UId. */
    private static final long serialVersionUID = 1L;

    /** the identifier of the phase. */
    private final String name;

    /**
     * The lifetime of the phase, which is the time the component or model is expected to spend in the current phase. This value
     * could be changed by the model behavior. When lifeTime is POSITIVE_INFINITY, the phase is passive, because we cannot go to
     * another phase, unless there is an external event. When it has a proper double value, the phase is active and we know when
     * the model is expected to move to another phase.
     */
    private double lifeTime = Double.POSITIVE_INFINITY;

    /**
     * The constructor of a new phase.
     * @param name String; the identifier of the phase
     */
    public Phase(final String name)
    {
        this.name = new String(name);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        String result;
        if (this.lifeTime == Double.POSITIVE_INFINITY)
        {
            result = " [ Passive phase] ";
        }
        else
        {
            result = " [ Active  phase] ";
        }
        return this.name + result;
    }

    /**
     * Sets the lifetime of the phase.
     * @param lifeTime double; the lifetime of the phase
     */
    public void setLifeTime(final double lifeTime)
    {
        this.lifeTime = lifeTime;
    }

    /**
     * @return the lifetime of the phase.
     */
    public double getLifeTime()
    {
        return this.lifeTime;
    }

    /**
     * @return name; the identifier of the phase
     */
    public String getName()
    {
        return this.name;
    }
}
