package nl.tudelft.simulation.dsol.formalisms.devs.esdevs;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;
import org.djutils.reflection.ClassUtil;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * AbstractDevsModel class. The basic model or component from which the AtomicModel, the CoupledModel, and the AbstractEntity
 * are derived. The DevsModel provides basic functionality for reporting its state changes through the publish/subscribe
 * mechanism.
 * <p>
 * Copyright (c) 2009-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="http://tudelft.nl/mseck">Mamadou Seck</a><br>
 * @author <a href="http://tudelft.nl/averbraeck">Alexander Verbraeck</a><br>
 * @param <T> the time type
 * @since 1.5
 */
public abstract class AbstractDevsModel<T extends Number & Comparable<T>> extends LocalEventProducer
{
    /** the default serial version UId. */
    private static final long serialVersionUID = 1L;

    /** the parent model we are part of. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected CoupledModel<T> parentModel;

    /** the simulator this model or component will schedule its events on. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected DevsSimulatorInterface<T> simulator;

    /** all DEVS models are named - this is the component name. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected String modelName;

    /** all DEVS models are named - this is the full name with dot notation. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected String fullName;

    /** event for listeners about state update. */
    public static final EventType STATE_UPDATE = new EventType(new MetaData("STATE_UPDATE", "State update",
            new ObjectDescriptor("stateUpdate", "State update", StateUpdate.class)));

    /** map of call classes and fields for which the state will be reported. */
    private static Map<Class<?>, Set<Field>> stateFieldMap = new LinkedHashMap<Class<?>, Set<Field>>();

    /** set of fields for this class which the state will be reported. */
    private Set<Field> stateFieldSet = null;

    /** the fields of the AtomicModel. */
    private static Set<Field> atomicFields = new LinkedHashSet<Field>();

    /** the fields of the CoupledModel. */
    private static Set<Field> coupledFields = new LinkedHashSet<Field>();

    /** the fields of the AbstractEntity. */
    private static Set<Field> entityFields = new LinkedHashSet<Field>();

    /** the fields of the AbstractDEVSMOdel. */
    private static Set<Field> abstractDEVSFields = new LinkedHashSet<Field>();

    /**
     * Static constructor. Takes care of filling the static constants the first time an extension of the AbstractDevsModel is
     * created.
     */
    static
    {
        AbstractDevsModel.atomicFields = ClassUtil.getAllFields(AtomicModel.class);
        AbstractDevsModel.coupledFields = ClassUtil.getAllFields(CoupledModel.class);
        AbstractDevsModel.entityFields = ClassUtil.getAllFields(AbstractEntity.class);
        AbstractDevsModel.abstractDEVSFields = ClassUtil.getAllFields(AbstractDevsModel.class);
    }

    /**
     * Constructor for an abstract DEVS model: we have to indicate the simulator to schedule the events on, and the parent model
     * we are part of. A parent model of null means that we are the top model.
     * @param modelName String; the name of this component
     * @param simulator DevsSimulatorInterface&lt;T&gt;; the simulator to schedule the events on.
     * @param parentModel CoupledModel&lt;T&gt;; the parent model we are part of.
     */
    public AbstractDevsModel(final String modelName, final DevsSimulatorInterface<T> simulator,
            final CoupledModel<T> parentModel)
    {
        this.modelName = modelName;
        if (parentModel != null)
        {
            this.fullName = parentModel.getFullName() + ".";
        }
        this.fullName += this.modelName;
        this.simulator = simulator;
        this.parentModel = parentModel;
        if (!AbstractDevsModel.stateFieldMap.containsKey(this.getClass()))
        {
            this.createStateFieldSet();
        }
        this.stateFieldSet = AbstractDevsModel.stateFieldMap.get(this.getClass());
    }

    /**
     * @return the simulator this model schedules its events on.
     */
    public DevsSimulatorInterface<T> getSimulator()
    {
        return this.simulator;
    }

    /**
     * @param simulator DevsSimulatorInterface&lt;T&gt;; the simulator to use from now on
     */
    public void setSimulator(final DevsSimulatorInterface<T> simulator)
    {
        this.simulator = simulator;
    }

    /**
     * @return the parent model we are part of.
     */
    public CoupledModel<T> getParentModel()
    {
        return this.parentModel;
    }

    /**
     * @return the name of the model.
     */
    public String getModelName()
    {
        return this.modelName;
    }

    /**
     * @return the full name of the model in dot notation.
     */
    public String getFullName()
    {
        return this.fullName;
    }

    @Override
    public String toString()
    {
        return this.fullName;
    }

    /**
     * Print the model, preceded by a user provided string.
     * @param header String; the user provided string to print in front of the model (e.g. newlines, header).
     */
    public abstract void printModel(String header);

    // ///////////////////////////////////////////////////////////////////////////
    // STATE CHANGE UPDATES, STATE SAVES
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * Create state field set. For this first version, take all the fields into account as state variables. The method substract
     * the fields that are on the level of AbstractModel or Atomic Model or higher; only leave the non-static fields that are
     * part of the descendents of the abstract models.
     */
    private void createStateFieldSet()
    {
        Set<Field> fieldSet = ClassUtil.getAllFields(this.getClass());

        if (this instanceof AtomicModel)
        {
            fieldSet.removeAll(AbstractDevsModel.atomicFields);
        }
        else if (this instanceof CoupledModel)
        {
            fieldSet.removeAll(AbstractDevsModel.coupledFields);
        }
        else if (this instanceof AbstractEntity)
        {
            fieldSet.removeAll(AbstractDevsModel.entityFields);
        }
        else
        {
            fieldSet.removeAll(AbstractDevsModel.abstractDEVSFields);
        }

        // we can now do something with the annotations, but that comes later

        // put the results in the map
        AbstractDevsModel.stateFieldMap.put(this.getClass(), fieldSet);
    }

    /**
     * Fire a state update. At this moment, all state variables are reported for an atomic model when it fires its
     * delta_internal or delta_external method. More intelligence can be added here later. For simple types, a comparison with
     * the old value (state map) is possible. For complex variables (objects) this is more difficult as a deep clone should be
     * saved as old state, followed by a full comparison. This does not seem practical, and more expensive than firing the state
     * change of all state variables. The intelligence to detect real state changes then has to be built in at the receiver's
     * side.
     */
    protected void fireUpdatedState()
    {
        for (Field field : this.stateFieldSet)
        {
            try
            {
                field.setAccessible(true);
                StateUpdate stateUpdate = new StateUpdate(this.getModelName(), field.getName(), field.get(this));
                this.fireTimedEvent(AbstractDevsModel.STATE_UPDATE, stateUpdate, getSimulator().getSimulatorTime());
            }
            catch (IllegalAccessException exception)
            {
                this.simulator.getLogger().always().error("Tried to fire update for variable {} but got an exception.",
                        field.getName());
                System.err.println(this.getModelName() + " - fireUpdateState: Tried to fire update for variable "
                        + field.getName() + " but got an exception.");
            }
        }
    }

    /**
     * StateUpdate class. Reports a state update. Right now, it is a modelname - variable name - value tuple.
     * <p>
     * Copyright (c) 2009-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>.
     * The DSOL project is distributed under a three-clause BSD-style license, which can be found at
     * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
     * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
     * </p>
     * @author <a href="http://tudelft.nl/mseck">Mamadou Seck</a><br>
     * @author <a href="http://tudelft.nl/averbraeck">Alexander Verbraeck</a><br>
     */
    public static class StateUpdate implements Serializable
    {
        /** the default serial version UId. */
        private static final long serialVersionUID = 1L;

        /** the name of the model. */
        private String model;

        /** the name of the variable. */
        private String variable;

        /** the value of the variable. */
        private Object value;

        /**
         * Construct a StateUPdate tuple to report a state update.
         * @param modelName String; the name of the model
         * @param variableName String; the name of the variable
         * @param value Object; the value
         */
        public StateUpdate(final String modelName, final String variableName, final Object value)
        {
            super();
            this.model = modelName;
            this.variable = variableName;
            this.value = value;
        }

        /**
         * @return the modelName
         */
        public String getModel()
        {
            return this.model;
        }

        /**
         * @return the variableName
         */
        public String getVariable()
        {
            return this.variable;
        }

        /**
         * @return the value
         */
        public Object getValue()
        {
            return this.value;
        }
    }

}
