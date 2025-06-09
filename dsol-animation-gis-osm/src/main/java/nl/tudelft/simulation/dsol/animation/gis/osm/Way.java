package nl.tudelft.simulation.dsol.animation.gis.osm;

import java.util.List;
import java.util.Map;

/**
 * Way as parsed from OSM.
 * <p>
 * Copyright (c) 2025-2025, Alexander Verbraeck, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class Way implements OsmEntity
{
    /** the id. */
    private final long id;

    /** the references. */
    private final List<Long> refs;

    /** the tags. */
    private final Map<String, String> tags;
    
    /**
     * @param id the id
     * @param refs the references
     * @param tags the tags
     */
    public Way(final long id, final List<Long> refs, final Map<String, String> tags)
    {
        this.id = id;
        this.refs = refs;
        this.tags = tags;
    }

    /**
     * @return id
     */
    public long getId()
    {
        return this.id;
    }

    /**
     * @return refs
     */
    public List<Long> getRefs()
    {
        return this.refs;
    }

    /**
     * @return tags
     */
    public Map<String, String> getTags()
    {
        return this.tags;
    }

    /**
     * Return whether the way is one-way.
     * @return whether the way is one-way
     */
    public boolean isOneWay()
    {
        String oneway = this.tags.get("oneway");
        return "yes".equals(oneway) || "true".equals(oneway) || "1".equals(oneway);
    }

    /**
     * Return whether the way is a road.
     * @return whether the way is a road
     */
    public boolean isRoad()
    {
        return this.tags.containsKey("highway");
    }
}
