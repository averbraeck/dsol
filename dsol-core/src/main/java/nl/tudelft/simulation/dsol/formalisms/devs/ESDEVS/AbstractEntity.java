package nl.tudelft.simulation.dsol.formalisms.devs.ESDEVS;

import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;

/**
 * AbstractEntity class. The AbstractEntity takes care of modeling components without behaviour but with state within coupled
 * models.
 * <p>
 * Copyright (c) 2009-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="http://tudelft.nl/mseck">Mamadou Seck</a><br>
 * @author <a href="http://tudelft.nl/averbraeck">Alexander Verbraeck</a><br>
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 */
public class AbstractEntity<T extends Number & Comparable<T>> extends AbstractDEVSModel<T>
{
    /** the default serial version UId. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for an abstract entity: we have to indicate the simulator for reporting purposes, and the parent model we are
     * part of. A parent model of null means that we are the top model.
     * @param modelName String; the name of this component
     * @param simulator DEVSSimulatorInterface&lt;A,R,T&gt;; the simulator for this model.
     * @param parentModel CoupledModel&lt;A,R,T&gt;; the parent model we are part of (can be null for highest level model).
     */
    public AbstractEntity(final String modelName, final DEVSSimulatorInterface<T> simulator, final CoupledModel<T> parentModel)
    {
        super(modelName, simulator, parentModel);
    }

    @Override
    public void printModel(final String header)
    {
        // TODO implement printmodel
    }

}
