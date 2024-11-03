package nl.tudelft.simulation.dsol.formalisms.devs.esdevs;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * AbstractEntity class. The AbstractEntity takes care of modeling components without behaviour but with state within coupled
 * models.
 * <p>
 * Copyright (c) 2009-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="http://tudelft.nl/mseck">Mamadou Seck</a><br>
 * @author <a href="http://tudelft.nl/averbraeck">Alexander Verbraeck</a><br>
 * @param <T> the time type
 */
public class AbstractEntity<T extends Number & Comparable<T>> extends AbstractDevsModel<T>
{
    /** the default serial version UId. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for an abstract entity: we have to indicate the simulator for reporting purposes, and the parent model we are
     * part of. A parent model of null means that we are the top model.
     * @param modelName String; the name of this component
     * @param simulator DevsSimulatorInterface&lt;T&gt;; the simulator for this model.
     * @param parentModel CoupledModel&lt;T&gt;; the parent model we are part of (can be null for highest level model).
     */
    public AbstractEntity(final String modelName, final DevsSimulatorInterface<T> simulator, final CoupledModel<T> parentModel)
    {
        super(modelName, simulator, parentModel);
    }

    @Override
    public void printModel(final String header)
    {
        // TODO implement printmodel
    }

}
