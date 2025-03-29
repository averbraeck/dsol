package nl.tudelft.simulation.dsol.animation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;
import org.djutils.event.LocalEventProducer;
import org.djutils.io.URLResource;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.language.d3.CartesianPoint;

/**
 * An Editable object is a simulation object that can be edited by the user. That means that the user is capable of
 * instantiating, moving, rotating, and editing the vertices that span up the shape of this object during the simulation.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @since 1.5
 */
public abstract class Editable extends LocalEventProducer implements Locatable
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** the static map of editables. */
    private static Map<Object, Object> editables = new LinkedHashMap<Object, Object>();

    // We read editables from a file called editable.properties
    // Editables read from this file will be made available to the user
    // for instantiation (e.g. in the Editor2D of the DSOL-GUI).
    static
    {
        try
        {
            Properties properties = new Properties();
            properties.load(URLResource.getResourceAsStream("/editable.properties"));
            Editable.editables.putAll(properties);
        }
        catch (Exception exception)
        {
            CategoryLogger.always().error(exception, "<clinit>");
        }
    }

    /**
     * the simulator to use.
     */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected SimulatorInterface<?> simulator = null;

    /**
     * the location of the editable.
     */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected OrientedPoint3d location = null;

    /**
     * the location of the bounds.
     */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Bounds3d bounds = new Bounds3d(1.0, 1.0, 1.0);

    /**
     * the vertices.
     */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected CartesianPoint[] vertices = new CartesianPoint[0];

    /**
     * returns the editables as a list of name=class.
     * @return the map
     */
    public static Map<Object, Object> listEditables()
    {
        return Editable.editables;
    }

    /**
     * constructs a new Editable.
     * @param simulator the simulator to schedule on
     * @param location the initial location
     */
    public Editable(final SimulatorInterface<?> simulator, final OrientedPoint3d location)
    {
        super();
        this.simulator = simulator;
        this.location = location;
    }

    /**
     * @return the vertices of the CartesianPoint.
     */
    public CartesianPoint[] getVertices()
    {
        return this.vertices;
    }

    /**
     * sets the bounds of this editable.
     * @param bounds the new bounds of this editable.
     */
    public void setBounds(final Bounds3d bounds)
    {
        this.bounds = bounds;
    }

    /**
     * sets the location of this editable.
     * @param location the new location of this editable
     */
    public void setLocation(final OrientedPoint3d location)
    {
        this.location = location;
    }

    /**
     * sets the vertices of this editable.
     * @param vertices the new vertices.
     */
    public void setVertices(final CartesianPoint[] vertices)
    {
        this.vertices = vertices;
    }

    @Override
    public Bounds3d getBounds()
    {
        return this.bounds;
    }

    @Override
    public OrientedPoint3d getLocation()
    {
        return this.location;
    }
}
