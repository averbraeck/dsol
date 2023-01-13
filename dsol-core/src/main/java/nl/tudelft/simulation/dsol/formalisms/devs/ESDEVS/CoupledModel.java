package nl.tudelft.simulation.dsol.formalisms.devs.ESDEVS;

import java.rmi.RemoteException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.djutils.event.EventListener;
import org.djutils.event.EventType;
import org.djutils.event.reference.Reference;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.devs.ESDEVS.exceptions.PortNotFoundException;
import nl.tudelft.simulation.dsol.simulators.DevsxSimulatorInterface;

/**
 * CoupledModel class. This class implements the classic parallel DEVS coupled model with ports conform Zeigler et al. (2000),
 * section 4.3.
 * <p>
 * Copyright (c) 2009-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="http://tudelft.nl/mseck">Mamadou Seck</a><br>
 * @author <a href="http://tudelft.nl/averbraeck">Alexander Verbraeck</a><br>
 * @param <T> the extended type itself to be able to implement a comparator on the simulation time.
 * @since 1.5
 */
public abstract class CoupledModel<T extends Number & Comparable<T>> extends AbstractDEVSPortModel<T>
{
    /** the default serialVersionUId. */
    private static final long serialVersionUID = 1L;

    /** the internal couplings (from internal models to internal models). */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Set<IC<T, ?>> internalCouplingSet = new LinkedHashSet<IC<T, ?>>();

    /** the couplings from the internal models to the output of this coupled model. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Set<EOC<T, ?>> externalOutputCouplingSet = new LinkedHashSet<EOC<T, ?>>();

    /** the couplings from the outside world to the internal models of this coupled model. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Set<EIC<T, ?>> externalInputCouplingSet = new LinkedHashSet<EIC<T, ?>>();

    /** the models within this coupled model. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Set<AbstractDEVSModel<T>> modelComponents = new LinkedHashSet<>();

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS AND INITIALIZATION
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * The constructor of the top model when the simulator is still unknown (e.g. in the constructModel() method).
     * @param modelName String; the name of this component
     */
    public CoupledModel(final String modelName)
    {
        super(modelName, null, null);
    }

    /**
     * The constructor of a coupled model within another coupled model.
     * @param modelName String; the name of this component
     * @param parentModel CoupledModel&lt;A,R,T&gt;; the parent coupled model for this model.
     */
    public CoupledModel(final String modelName, final CoupledModel<T> parentModel)
    {
        super(modelName, parentModel.getSimulator(), parentModel);
        if (this.parentModel != null)
        {
            this.parentModel.addModelComponent(this);
        }
    }

    /**
     * Constructor of a high-level coupled model without a parent model.
     * @param modelName String; the name of this component
     * @param simulator DEVSSimulatorInterface&lt;A,R,T&gt;; the simulator to schedule events on.
     */
    public CoupledModel(final String modelName, final DevsxSimulatorInterface<T> simulator)
    {
        super(modelName, simulator, null);

    }

    /**
     * Add a listener recursively to the model and all its submodels. Delegate it for this coupled model to the embedded event
     * producer.
     * @param eli EventListener; the event listener.
     * @param et EventType; the event type.
     * @return success or failure of adding the listener to all submodels.
     */
    public boolean addHierarchicalListener(final EventListener eli, final EventType et)
    {
        boolean returnBoolean = true;
        returnBoolean &= super.addListener(eli, et);

        for (AbstractDEVSModel<T> devsmodel : this.modelComponents)
        {
            returnBoolean &= devsmodel.addListener(eli, et);
        }

        return returnBoolean;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // TRANSFER FUNCTIONS
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * The transfer function takes care of transferring a value from this coupled model to the outside world.
     * @param <TYPE> the type of message / event being transferred
     * @param x OutputPortInterface&lt;A,R,T,TYPE&gt;; the output port through which the transfer takes place
     * @param y TYPE; the value being transferred
     * @throws RemoteException remote exception
     * @throws SimRuntimeException simulation run time exception
     */
    @SuppressWarnings("unchecked")
    public <TYPE> void transfer(final OutputPortInterface<T, TYPE> x, final TYPE y) throws RemoteException, SimRuntimeException
    {
        for (IC<T, ?> o : this.internalCouplingSet)
        {
            if (o.getFromPort() == x)
            {
                ((IC<T, TYPE>) o).getToPort().receive(y, this.simulator.getSimulatorTime());
            }
        }
        for (EOC<T, ?> o : this.externalOutputCouplingSet)
        {
            if (o.getFromPort() == x)
            {
                ((EOC<T, TYPE>) o).getToPort().send(y);
            }
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // COUPLING: MAKING AND REMOVING IC, EOC, EIC COUPLINGS
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * @param <TYPE> the type of message / event for which the coupling is added.
     * @param fromPort OutputPortInterface&lt;A,R,T,TYPE&gt;; the output port of an internal component that transfers the
     *            message / event to another internal component (start of the coupling)
     * @param toPort InputPortInterface&lt;A,R,T,TYPE&gt;; the input port of an internal component that receives a message /
     *            event from the other componet (end of the coupling)
     */
    public <TYPE> void addInternalCoupling(final OutputPortInterface<T, TYPE> fromPort,
            final InputPortInterface<T, TYPE> toPort)
    {
        try
        {
            this.internalCouplingSet.add(new IC<T, TYPE>(fromPort, toPort));
        }
        catch (Exception e)
        {
            this.simulator.getLogger().always().error(e);
        }

    }

    /**
     * @param <TYPE> the type of message / event for which the coupling is removed.
     * @param fromPort OutputPortInterface&lt;A,R,T,TYPE&gt;; the output port of an internal component that transfers the
     *            message / event to another internal component (start of the coupling)
     * @param toPort InputPortInterface&lt;A,R,T,TYPE&gt;; the input port of an internal component that receives a message /
     *            event from the other componet (end of the coupling)
     */
    public <TYPE> void removeInternalCoupling(final OutputPortInterface<T, TYPE> fromPort,
            final InputPortInterface<T, TYPE> toPort)
    {
        for (IC<T, ?> ic : this.internalCouplingSet)
        {
            if (ic.getFromPort().getModel() == fromPort && ic.getToPort().getModel() == toPort)
            {
                this.internalCouplingSet.remove(ic);
            }
        }

    }

    /**
     * Add an IOC within this coupled model.
     * @param <TYPE> the type of message / event for which the coupling is added.
     * @param fromPort InputPortInterface&lt;A,R,T,TYPE&gt;; the input port of this coupled model that transfers the message /
     *            event to the internal component (start of the coupling)
     * @param toPort InputPortInterface&lt;A,R,T,TYPE&gt;; the input port of the internal component that receives a message /
     *            event from the overarching coupled model (end of the coupling)
     */
    public <TYPE> void addExternalInputCoupling(final InputPortInterface<T, TYPE> fromPort,
            final InputPortInterface<T, TYPE> toPort)
    {
        try
        {
            this.externalInputCouplingSet.add(new EIC<T, TYPE>(fromPort, toPort));
        }
        catch (Exception e)
        {
            this.simulator.getLogger().always().error(e);
        }
    }

    /**
     * Remove an IOC within this coupled model.
     * @param <TYPE> the type of message / event for which the coupling is removed.
     * @param fromPort InputPortInterface&lt;A,R,T,TYPE&gt;; the input port of this coupled model that transfers the message /
     *            event to the internal component (start of the coupling)
     * @param toPort InputPortInterface&lt;A,R,T,TYPE&gt;; the input port of the internal component that receives a message /
     *            event from the overarching coupled model (end of the coupling)
     */
    public <TYPE> void removeExternalInputCoupling(final InputPortInterface<T, TYPE> fromPort,
            final InputPortInterface<T, TYPE> toPort)
    {
        for (EIC<T, ?> eic : this.externalInputCouplingSet)
        {
            if (eic.getFromPort() == fromPort && eic.getToPort() == toPort)
            {
                this.externalInputCouplingSet.remove(eic);
            }
        }
    }

    /**
     * Add an EOC within this coupled model.
     * @param <TYPE> the type of message / event for which the coupling is added.
     * @param fromPort OutputPortInterface&lt;A,R,T,TYPE&gt;; the output port of the internal component that produces an event
     *            for the outside of the overarching coupled model (start of the coupling)
     * @param toPort OutputPortInterface&lt;A,R,T,TYPE&gt;; the output port of this coupled model that transfers the message /
     *            event to the outside (end of the coupling)
     */
    public <TYPE> void addExternalOutputCoupling(final OutputPortInterface<T, TYPE> fromPort,
            final OutputPortInterface<T, TYPE> toPort)
    {
        try
        {
            this.externalOutputCouplingSet.add(new EOC<T, TYPE>(fromPort, toPort));
        }
        catch (Exception e)
        {
            this.simulator.getLogger().always().error(e);
        }
    }

    /**
     * Remove an EOC within this coupled model.
     * @param <TYPE> the type of message / event for which the coupling is removed.
     * @param fromPort OutputPortInterface&lt;A,R,T,TYPE&gt;; the output port of the internal component that produces an event
     *            for the outside of the overarching coupled model (start of the coupling)
     * @param toPort OutputPortInterface&lt;A,R,T,TYPE&gt;; the output port of this coupled model that transfers the message /
     *            event to the outside (end of the coupling)
     */
    public <TYPE> void removeExternalOutputCoupling(final OutputPortInterface<T, TYPE> fromPort,
            final OutputPortInterface<T, TYPE> toPort)
    {
        for (EOC<T, ?> eoc : this.externalOutputCouplingSet)
        {
            if (eoc.getFromPort() == fromPort && eoc.getToPort() == toPort)
            {
                this.externalOutputCouplingSet.remove(eoc);
            }
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // STRUCTURE: ADDING AND REMOVING COMPONENTS AND PORTS
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * Add a model component to this coupled model.
     * @param model AbstractDEVSModel&lt;A,R,T&gt;; the component to add.
     */
    public void addModelComponent(final AbstractDEVSModel<T> model)
    {
        this.modelComponents.add(model);

        List<Reference<EventListener>> elis = getListenerReferences(AbstractDEVSModel.STATE_UPDATE);

        if (elis == null)
        {
            return;
        }

        for (Reference<EventListener> eli : elis)
        {
            model.addListener(eli.get(), AbstractDEVSModel.STATE_UPDATE);
        }
    }

    /**
     * Remove a model component from a coupled model, including all its couplings (internal, external in, and external out).
     * @param model AbstractDEVSModel&lt;A,R,T&gt;; the component to remove.
     */
    public void removeModelComponent(final AbstractDEVSModel<T> model)
    {
        for (EOC<T, ?> eoc : this.externalOutputCouplingSet)
        {
            if (eoc.getFromPort().getModel() == model || eoc.getToPort().getModel() == model)
            {
                this.externalOutputCouplingSet.remove(eoc);
            }
        }

        for (EIC<T, ?> eic : this.externalInputCouplingSet)
        {
            if (eic.getFromPort().getModel() == model || eic.getToPort().getModel() == model)
            {
                this.externalInputCouplingSet.remove(eic);
            }
        }

        for (IC<T, ?> ic : this.internalCouplingSet)
        {
            if (ic.getFromPort().getModel() == model || ic.getToPort().getModel() == model)
            {
                this.internalCouplingSet.remove(ic);
            }
        }

        // this will also take care of the removal of the ports as they are not
        // connected to anything anymore.

        this.modelComponents.remove(model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeInputPort(final String name) throws PortNotFoundException
    {
        InputPortInterface<T, ?> inputPort = this.inputPortMap.get(name);
        super.removeInputPort(name); // throws exception in case nonexistent

        for (EIC<T, ?> eic : this.externalInputCouplingSet)
        {
            if (eic.getFromPort() == inputPort || eic.getToPort() == inputPort)
            {
                this.externalInputCouplingSet.remove(eic);
            }
        }

        for (IC<T, ?> ic : this.internalCouplingSet)
        {
            if (ic.getToPort() == inputPort)
            {
                this.internalCouplingSet.remove(ic);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeOutputPort(final String name) throws PortNotFoundException
    {
        OutputPortInterface<T, ?> outputPort = this.outputPortMap.get(name);
        super.removeOutputPort(name); // throws exception in case nonexistent

        for (EOC<T, ?> eoc : this.externalOutputCouplingSet)
        {
            if (eoc.getFromPort() == outputPort || eoc.getToPort() == outputPort)
            {
                this.externalOutputCouplingSet.remove(eoc);
            }
        }

        for (IC<T, ?> ic : this.internalCouplingSet)
        {
            if (ic.getFromPort() == outputPort)
            {
                this.internalCouplingSet.remove(ic);
            }
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // GETTERS FOR THE STRUCTURE
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * @return internalCouplingSet; the internal couplings (from internal models to internal models)
     */
    public Set<IC<T, ?>> getInternalCouplingSet()
    {
        return this.internalCouplingSet;
    }

    /**
     * @return externalOutputCouplingSet; the couplings from the internal models to the output of this coupled model
     */
    public Set<EOC<T, ?>> getExternalOutputCouplingSet()
    {
        return this.externalOutputCouplingSet;
    }

    /**
     * @return externalInputCouplingSet; the couplings from the outside world to the internal models of this coupled model
     */
    public Set<EIC<T, ?>> getExternalInputCouplingSet()
    {
        return this.externalInputCouplingSet;
    }

    /**
     * @return modelComponents; the models within the coupled model
     */
    public Set<AbstractDEVSModel<T>> getModelComponents()
    {
        return this.modelComponents;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // PRINTING THE MODEL
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public void printModel(final String space)
    {
        System.out.println(space + "================");
        System.out.println(space + "coupled model name: " + this.getClass().getName());
        System.out.println(space + "Externaloutputcouplings");
        for (EOC<T, ?> eoc : this.externalOutputCouplingSet)
        {
            System.out.print(space);
            System.out.print("between ");
            System.out.print(eoc.getFromPort().getModel().getClass().getName());
            System.out.print(" and ");
            System.out.print(eoc.getToPort().getModel().getClass().getName());
            System.out.println();
        }
        System.out.println(space + "Externalinputcouplings");
        for (EIC<T, ?> eic : this.externalInputCouplingSet)
        {
            System.out.print(space);
            System.out.print("between ");
            System.out.print(eic.getFromPort().getModel().getClass().getName());
            System.out.print(" and ");
            System.out.print(eic.getToPort().getModel().getClass().getName());
            System.out.println();
        }
        System.out.println(space + "Externaloutputcouplings");
        for (IC<T, ?> ic : this.internalCouplingSet)
        {
            System.out.print(space);
            System.out.print("between ");
            System.out.print(ic.getFromPort().getModel().getClass().getName());
            System.out.print(" and ");
            System.out.print(ic.getToPort().getModel().getClass().getName());
            System.out.println();
        }

        for (AbstractDEVSModel<T> dm : this.modelComponents)
        {
            dm.printModel(space + "    ");
        }
        System.out.println(space + "================");
    }

}
