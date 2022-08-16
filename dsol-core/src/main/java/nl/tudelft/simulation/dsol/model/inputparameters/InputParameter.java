package nl.tudelft.simulation.dsol.model.inputparameters;

import java.io.Serializable;

/**
 * User readable and settable properties.
 * <p>
 * Copyright (c) 2013-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="http://opentrafficsim.org/docs/current/license.html">OpenTrafficSim License</a>.
 * <p>
 * @version $Revision$, $LastChangedDate$, by $Author$, initial version Apr 22, 2016 <br>
 * @author <a href="http://www.tbm.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 * @author <a href="http://www.transport.citg.tudelft.nl">Wouter Schakel</a>
 * @param <VT> Value type of the input parameter
 * @param <CT> Calculated type of the input parameter (often the same as VT, except in complex maps)
 */
public interface InputParameter<VT, CT> extends Serializable, Cloneable
{
    /**
     * Retrieve the key of this InputParameter. The key is set at time of construction and it is immutable.
     * @return String; the key of this InputParameter
     */
    String getKey();

    /**
     * Retrieve the extended key of this AbstractInputParameter including parents with a dot-notation.
     * @return String; the extended key of this AbstractInputParameter
     */
    String getExtendedKey();

    /**
     * Retrieve the current value of the input parameter.
     * @return VT; the current value of the input parameter
     */
    VT getValue();

    /**
     * Retrieve the default value of the input parameter.
     * @return VT; the default value of the input parameter
     */
    VT getDefaultValue();

    /**
     * Change the default value of the input parameter.
     * @param newValue VT; the new default value for the input parameter
     * @throws NullPointerException when newValue is null
     * @throws InputParameterException when this InputParameter is read-only, or newValue is not valid
     */
    void setDefaultValue(VT newValue) throws InputParameterException;

    /**
     * Retrieve the calculated value of the input parameter. This is often the same as the value type, except in situations
     * where a sub-map or sub-list calculates a different value. This is, for instance, the case with distribution functions
     * where the parameters are present in a sub-map (the value) and the return type of the value is a distribution function
     * class.
     * @return CT; the calculated value of the input parameter
     * @throws InputParameterException when calculation fails
     */
    CT getCalculatedValue() throws InputParameterException;

    /**
     * Return a short description of the input parameter.
     * @return String; a short description of the input parameter
     */
    String getShortName();

    /**
     * Return a description of the input parameter (may use HTML markup).
     * @return String; the description of the input parameter
     */
    String getDescription();

    /**
     * Specify if the input parameter can be altered.
     * @param readOnly boolean; true if this input parameter can not be altered, false if this input parameter can be altered
     */
    void setReadOnly(boolean readOnly);

    /**
     * Return true if the input parameter can not be altered.
     * @return boolean; true if this input parameter can not be altered, false if this input parameter can be altered
     */
    boolean isReadOnly();

    /**
     * Display priority determines the order in which properties should be displayed. Properties with lower values should be
     * displayed above or before those with higher values.
     * @return double; the display priority of this InputParameter
     */
    double getDisplayPriority();

    /**
     * Retrieve the parent input parameter.
     * @return the InputParameterMap that is the parent of this InputParameter (result is null if this input parameter is not
     *         contained in an InputParameterMap)
     */
    AbstractInputParameterMap<?> getParent();

    /**
     * Provide the clone() method to make a deep copy.
     * @return a deep copy of the InputParameter
     */
    InputParameter<?, ?> clone();
}
