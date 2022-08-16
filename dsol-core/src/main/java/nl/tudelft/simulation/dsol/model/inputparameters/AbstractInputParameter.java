package nl.tudelft.simulation.dsol.model.inputparameters;

import org.djutils.exceptions.Throw;

/**
 * Abstract input parameter.
 * <p>
 * Copyright (c) 2013-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="http://opentrafficsim.org/docs/license.html">OpenTrafficSim License</a>.
 * <p>
 * $LastChangedDate: 2016-05-28 11:33:31 +0200 (Sat, 28 May 2016) $, @version $Revision: 2051 $, by $Author: averbraeck $,
 * initial version 18 dec. 2014 <br>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 * @param <VT> Value type of the input parameter
 * @param <CT> Calculated type of the input parameter (often the same as VT, except in complex maps)
 */
public abstract class AbstractInputParameter<VT, CT> implements InputParameter<VT, CT>
{
    /** */
    private static final long serialVersionUID = 20150000L;

    /** Key of this input parameter. */
    private final String key;

    /** The shortName of the input parameter. */
    private String shortName;

    /** The description of the input parameter. */
    private String description;

    /** the default value. */
    private VT defaultValue;

    /** the current value. */
    private VT value;

    /** Determines sorting order when properties are displayed to the user. */
    private final double displayPriority;

    /** The input parameter is read-only. */
    private boolean readOnly = false;

    /** Parent of this AbstractInputParameter. */
    private AbstractInputParameterMap<?> parent = null;

    /**
     * Construct a new AbstractInputParameter.
     * @param key String; unique (within this input parameter tree) name of the new AbstractInputParameter
     * @param shortName String; concise description of the input parameter
     * @param description String; long description of the input parameter (may use HTML markup)
     * @param defaultValue VT; the default value of this input parameter
     * @param displayPriority double; sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, or description is null
     * @throws IllegalArgumentException when displayPriority is NaN, or when key contains a period
     */
    public AbstractInputParameter(final String key, final String shortName, final String description, final VT defaultValue,
            final double displayPriority)
    {
        Throw.whenNull(key, "key should not be null");
        Throw.when(key.contains("."), IllegalArgumentException.class, "key should not contain a period");
        Throw.whenNull(shortName, "shortName should not be null");
        Throw.whenNull(description, "description should not be null");
        Throw.whenNull(defaultValue, "description should not be null");
        Throw.when(Double.isNaN(displayPriority), IllegalArgumentException.class, "displayPriority shouldnot be NaN");
        this.key = key;
        this.shortName = shortName;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.displayPriority = displayPriority;
    }

    /** {@inheritDoc} */
    @Override
    public String getKey()
    {
        return this.key;
    }

    /** {@inheritDoc} */
    @Override
    public String getExtendedKey()
    {
        return (getParent() == null) ? getKey() : getParent().getExtendedKey() + "." + getKey();
    }

    /** {@inheritDoc} */
    @Override
    public VT getValue()
    {
        return this.value;
    }

    /**
     * Change the value of the input parameter. This method is protected and final, so classes that extend this class must use
     * their own method to set the value (e.g., setDoubleValue(...)), which has to call, in turn, super.setValue(...) to make
     * the actual setting of the value happen. In case the setValue(...) method would be non-final and public, it would be too
     * easy to forget to call super.setValue(...).
     * @param newValue VT; the new value for the input parameter
     * @throws NullPointerException when newValue is null
     * @throws InputParameterException when this InputParameter is read-only, or newValue is not valid
     */
    public void setValue(final VT newValue) throws InputParameterException
    {
        Throw.whenNull(newValue, "InputParameter.setValue not allowed with null argument");
        if (isReadOnly())
        {
            throw new InputParameterException("The InputParameter with key " + getExtendedKey() + " is read-only");
        }
        this.value = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public VT getDefaultValue()
    {
        return this.defaultValue;
    }

    /** {@inheritDoc} */
    @Override
    public void setDefaultValue(final VT newValue) throws InputParameterException
    {
        Throw.whenNull(newValue, "InputParameter.setDefaultValue not allowed with null argument");
        this.defaultValue = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public void setReadOnly(final boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    /** {@inheritDoc} */
    @Override
    public double getDisplayPriority()
    {
        return this.displayPriority;
    }

    /** {@inheritDoc} */
    @Override
    public String getShortName()
    {
        return this.shortName;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription()
    {
        return this.description;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isReadOnly()
    {
        return this.readOnly;
    }

    /**
     * Set the parent of this AbstractInputParameter.
     * @param newParent AbstractInputParameterMap&lt;?&gt;; the new parent of this AbstractInputParameter
     */
    public void setParent(final AbstractInputParameterMap<?> newParent)
    {
        this.parent = newParent;
    }

    /** {@inheritDoc} */
    @Override
    public AbstractInputParameterMap<?> getParent()
    {
        return this.parent;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return getKey() + "[" + getShortName() + "] = " + getValue();
    }

    /** {@inheritDoc} */
    @Override
    public AbstractInputParameter<?, ?> clone()
    {
        try
        {
            return (AbstractInputParameter<?, ?>) super.clone();
        }
        catch (CloneNotSupportedException cnse)
        {
            throw new RuntimeException("Clone AbstractInputParameter", cnse);
        }
    }

}
