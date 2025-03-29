package nl.tudelft.simulation.dsol.formalisms.devs.esdevs;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.tudelft.simulation.dsol.formalisms.devs.esdevs.exceptions.PortAlreadyDefinedException;
import nl.tudelft.simulation.dsol.formalisms.devs.esdevs.exceptions.PortNotFoundException;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * AbstractDevsPortModel class. Adds named ports to the abstract DEVS model.
 * <p>
 * Copyright (c) 2009-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="http://tudelft.nl/mseck">Mamadou Seck</a><br>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a><br>
 * @param <T> the time type
 * @since 1.5
 */
public abstract class AbstractDevsPortModel<T extends Number & Comparable<T>> extends AbstractDevsModel<T>
{
    /** the default serial version UId. */
    private static final long serialVersionUID = 1L;

    /** the map of input port names to input ports. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Map<String, InputPortInterface<T, ?>> inputPortMap = new LinkedHashMap<>();

    /** the map of output port names to output ports. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Map<String, OutputPortInterface<T, ?>> outputPortMap = new LinkedHashMap<>();

    /**
     * Constructor for an abstract DEVS model with ports: we have to indicate the simulator to schedule the events on, and the
     * parent model we are part of. A parent model of null means that we are the top model.
     * @param modelName the name of this component
     * @param simulator the simulator to schedule the events on.
     * @param parentModel the parent model we are part of.
     */
    public AbstractDevsPortModel(final String modelName, final DevsSimulatorInterface<T> simulator,
            final CoupledModel<T> parentModel)
    {
        super(modelName, simulator, parentModel);
    }

    /**
     * Add an input port to the model. Use a name to be able to identify the port later.
     * @param name the (unique) name of the input port
     * @param inputPort the input port to add
     * @param <TYPE> the type of variable of the input port
     * @throws PortAlreadyDefinedException in case the port name already exist for the model
     */
    protected <TYPE> void addInputPort(final String name, final InputPortInterface<T, TYPE> inputPort)
            throws PortAlreadyDefinedException
    {
        if (this.inputPortMap.containsKey(name))
        {
            throw new PortAlreadyDefinedException(
                    "Adding port " + name + " for model " + this.toString() + ", but it already exists.");
        }
        this.inputPortMap.put(name, inputPort);
    }

    /**
     * Add an output port to the model. Use a name to be able to identify the port later.
     * @param name the (unique) name of the output port
     * @param outputPort the output port to add
     * @param <TYPE> the type of variable of the output port
     * @throws PortAlreadyDefinedException in case the port name already exist for the model
     */
    protected <TYPE> void addOutputPort(final String name, final OutputPortInterface<T, TYPE> outputPort)
            throws PortAlreadyDefinedException
    {
        if (this.outputPortMap.containsKey(name))
        {
            throw new PortAlreadyDefinedException(
                    "Adding port " + name + " for model " + this.toString() + ", but it already exists.");
        }
        this.outputPortMap.put(name, outputPort);
    }

    /**
     * Remove an input port from the model. Note: override this method in classes that extend the behavior, e.g. to remove
     * couplings from this port in case it is removed.
     * @param name the name of the input port to be removed
     * @throws PortNotFoundException in case the port name does not exist for the model
     */
    protected void removeInputPort(final String name) throws PortNotFoundException
    {
        if (!this.inputPortMap.containsKey(name))
        {
            throw new PortNotFoundException(
                    "Removing port " + name + " for model " + this.toString() + ", but it does not exist.");
        }
        this.inputPortMap.remove(name);
    }

    /**
     * Remove an output port from the model. Note: override this method in classes that extend the behavior, e.g. to remove
     * couplings from this port in case it is removed.
     * @param name the name of the output port to be removed
     * @throws PortNotFoundException in case the port name does not exist for the model
     */
    protected void removeOutputPort(final String name) throws PortNotFoundException
    {
        if (!this.outputPortMap.containsKey(name))
        {
            throw new PortNotFoundException(
                    "Removing port " + name + " for model " + this.toString() + ", but it does not exist.");
        }
        this.outputPortMap.remove(name);
    }

    /**
     * @return the map of input port names to input ports.
     */
    public Map<String, InputPortInterface<T, ?>> getInputPortMap()
    {
        return this.inputPortMap;
    }

    /**
     * @return the map of output port names to output ports
     */
    public Map<String, OutputPortInterface<T, ?>> getOutputPortMap()
    {
        return this.outputPortMap;
    }
}
