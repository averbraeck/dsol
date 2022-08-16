package nl.tudelft.simulation.dsol.animation.gis;

import java.io.Serializable;

/**
 * the GisObject class stores a Point2D or Shape as well as an array of attributes.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class GisObject implements Serializable
{
    /** */
    private static final long serialVersionUID = 20201015L;

    /** the shape, which is either a Point2D or a Shape. */
    private Object shape;

    /** the attributes. */
    private String[] attributes;

    /**
     * constructs a GisObject.
     * @param shape Object; the shape (either a <code>java.awt.geom.Point2D</code> or a <code>java.awt.Shape</code>
     * @param attributes String[]; attributes for this point or shape
     */
    public GisObject(final Object shape, final String[] attributes)
    {
        this.shape = shape;
        this.attributes = attributes;
    }

    /**
     * returns the point or shape of the GisObject.
     * @return Object the stored shape
     */
    public Object getShape()
    {
        return this.shape;
    }

    /**
     * returns the attribute values of the shape. The index in the String[] belongs to the attributeKeys in the DataSource.
     * @return String[]; the array of Strings representing the attributes of the GisObject
     */
    public String[] getAttributeValues()
    {
        return this.attributes;
    }
}
