package nl.tudelft.simulation.dsol.animation.gis.osm;

/**
 * Node as parsed from OSM.
 * <p>
 * Copyright (c) 2025-2025, Alexander Verbraeck, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class Node implements OsmEntity
{
    /** the node id. */
    private final long id;

    /** the latitude of the node. */
    private final double lat;

    /** the longitude of the node. */
    private final double lon;

    /**
     * Create a node.
     * @param id the id
     * @param lat the latitude
     * @param lon the longitude
     */
    public Node(final long id, final double lat, final double lon)
    {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * @return id
     */
    public long getId()
    {
        return this.id;
    }

    /**
     * @return lat
     */
    public double getLat()
    {
        return this.lat;
    }

    /**
     * @return lon
     */
    public double getLon()
    {
        return this.lon;
    }

    @Override
    public boolean equals(final Object o)
    {
        return o instanceof Node && ((Node) o).id == this.id;
    }

    @Override
    public int hashCode()
    {
        return Long.hashCode(this.id);
    }

    @Override
    public String toString()
    {
        return String.format("Node(%d, %.6f, %.6f)", this.id, this.lat, this.lon);
    }
}
