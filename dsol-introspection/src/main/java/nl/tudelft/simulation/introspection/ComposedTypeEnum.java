package nl.tudelft.simulation.introspection;

/**
 * ComposedTypeEnum contains the type of a composed Property.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public enum ComposedTypeEnum
{
    /** Not composed. */
    NONE,
    /** Java Array. */
    ARRAY,
    /** General Java Collection. */
    COLLECTION,
    /** Immutable Collection. */
    IMMUTABLECOLLECTION,
    /** General Java Map. */
    MAP,
    /** Immutable Map. */
    IMMUTABLEMAP;

    /**
     * @return whether the enum is a collection or not
     */
    public boolean isComposed()
    {
        return !this.equals(NONE);
    }

    /**
     * @return whether the enum is a Java array
     */
    public boolean isArray()
    {
        return this.equals(ARRAY);
    }

    /**
     * @return whether the enum is a Java Collection
     */
    public boolean isCollection()
    {
        return this.equals(COLLECTION);
    }

    /**
     * @return whether the enum is a Java Map
     */
    public boolean isMap()
    {
        return this.equals(MAP);
    }

    /**
     * @return whether the enum is an ImmutableCollection
     */
    public boolean isImmutableCollection()
    {
        return this.equals(IMMUTABLECOLLECTION);
    }

    /**
     * @return whether the enum is an ImmutableMap
     */
    public boolean isImmutableMap()
    {
        return this.equals(IMMUTABLEMAP);
    }

}
