package nl.tudelft.simulation.dsol.animation.gis.osm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Relation stores the OSM information about relations, such as multipolygons and borders. We store the relation id, its members
 * and tags.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Relation implements OsmEntity
{
    /** The type of member of the Relation. */
    public static enum Type
    {
        /** Node. */
        NODE,

        /** Way. */
        WAY,

        /** Relation-in-relation. */
        RELATION
    }

    /**
     * The member and role, such as "inner" or "outer".
     * @param type the type of member (NODE, WAY, or other RELATION).
     * @param ref reference to node, way or other relation
     * @param role role such as "inner" or "outer"
     */
    public static record Member(Type type, long ref, String role)
    {
    }

    /** Relation id. */
    private final long id;

    /** Relation members (nodes, ways or other relations) with their roles. */
    private final List<Member> members = new ArrayList<>();

    /** Relation tags. */
    private final Map<String, String> tags = new HashMap<>();

    /**
     * Create an OSM Relation.
     * @param id the Relation id
     */
    public Relation(final long id)
    {
        this.id = id;
    }

    /**
     * Add a member to the Relation.
     * @param member the member to add
     */
    public void addMember(final Member member)
    {
        this.members.add(member);
    }

    /**
     * Add a tag to the Relation.
     * @param key the key of the tag
     * @param value the value of the tag
     */
    public void addTag(final String key, final String value)
    {
        this.tags.put(key, value);
    }

    /**
     * Return the relation id.
     * @return the id
     */
    public long getId()
    {
        return this.id;
    }

    /**
     * Return the relation's members.
     * @return the members
     */
    public List<Member> getMembers()
    {
        return this.members;
    }

    /**
     * Return the relation's tags.
     * @return the tags
     */
    public Map<String, String> getTags()
    {
        return this.tags;
    }

}
