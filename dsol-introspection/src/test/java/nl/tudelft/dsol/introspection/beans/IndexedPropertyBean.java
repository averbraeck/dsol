/*
 * IndexedPropertyBean.java Created on July 5, 2001, 5:06 PM
 */

package nl.tudelft.dsol.introspection.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

/**
 * <p>
 * Copyright (c) 2004-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="http://web.eur.nl/fbk/dep/dep1/Introduction/Staff/People/Lang">Niels Lang
 *         </a><a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @since 1.5
 */
public class IndexedPropertyBean extends Object
{

    /** Holds value of property indexedProp. */
    private String[] indexedProp = new String[] {"test1", "test2"};

    /** Holds value of property vector. */
    private Vector vector;

    /** Holds value of property collection. */
    private java.util.HashMap collection;

    /** Holds value of property beans. */
    private TestBean[] beans;

    /** Holds value of property doubleValue. */
    private Double doubleValue = Double.valueOf(1717);

    /** Holds value of property serializedDoubleValue. */
    private java.io.Serializable serializedDoubleValue = Double.valueOf(1717);

    /** Creates new IndexedPropertyBean. */
    public IndexedPropertyBean()
    {
        this.indexedProp[0] = "nul";
        this.indexedProp[1] = "notnul";
    }

    /**
     * Indexed getter for property indexedProp.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public String getIndexedProp(final int index)
    {
        return this.indexedProp[index];
    }

    /**
     * Getter for property indexedProp.
     * @return Value of property indexedProp.
     */
    public String[] getIndexedProp()
    {
        return this.indexedProp;
    }

    /**
     * Indexed setter for property indexedProp.
     * @param index Index of the property.
     * @param indexedProp New value of the property at <CODE>index</CODE>.
     */
    public void setIndexedProp(final int index, final String indexedProp)
    {
        this.indexedProp[index] = indexedProp;
    }

    /**
     * Setter for property indexedProp.
     * @param indexedProp New value of property indexedProp.
     */
    public void setIndexedProp(final String[] indexedProp)
    {
        this.indexedProp = indexedProp;
    }

    /**
     * Getter for property vector.
     * @return Value of property vector.
     */
    public Vector getVector()
    {
        return this.vector;
    }

    /**
     * Setter for property vector.
     * @param vector New value of property vector.
     */
    public void setVector(final Vector vector)
    {
        this.vector = vector;
    }

    /**
     * Getter for property collection.
     * @return Value of property collection.
     */
    public java.util.HashMap getCollection()
    {
        return this.collection;
    }

    /**
     * Setter for property collection.
     * @param collection New value of property collection.
     */
    public void setCollection(final HashMap collection)
    {
        this.collection = collection;
    }

    /**
     * Getter for property beans.
     * @return Value of property beans.
     */
    public TestBean[] getBeans()
    {
        return this.beans;
    }

    /**
     * Setter for property beans.
     * @param beans New value of property beans.
     */
    public void setBeans(final TestBean[] beans)
    {
        this.beans = beans;
    }

    /**
     * Getter for property doubleValue.
     * @return Value of property doubleValue.
     */
    public Double getDoubleValue()
    {
        return this.doubleValue;
    }

    /**
     * Setter for property doubleValue.
     * @param doubleValue New value of property doubleValue.
     */
    public void setDoubleValue(final Double doubleValue)
    {
        this.doubleValue = doubleValue;
    }

    /**
     * Getter for property serializedDoubleValue.
     * @return Value of property serializedDoubleValue.
     */
    public java.io.Serializable getSerializedDoubleValue()
    {
        return this.serializedDoubleValue;
    }

    /**
     * Setter for property serializedDoubleValue.
     * @param serializedDoubleValue New value of property serializedDoubleValue.
     */
    public void setSerializedDoubleValue(final Serializable serializedDoubleValue)
    {
        this.serializedDoubleValue = serializedDoubleValue;
    }

}
