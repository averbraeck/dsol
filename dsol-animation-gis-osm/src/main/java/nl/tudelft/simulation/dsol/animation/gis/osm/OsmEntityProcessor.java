package nl.tudelft.simulation.dsol.animation.gis.osm;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.tudelft.simulation.dsol.animation.gis.FeatureInterface;
import nl.tudelft.simulation.dsol.animation.gis.FloatXY;
import nl.tudelft.simulation.dsol.animation.gis.transform.CoordinateTransform;

/**
 * OsmEntityProcessor processes one read entity from the input file.
 * <p>
 * Copyright (c) 2021-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class OsmEntityProcessor
{
    /** the nodes in the OSM file. */
    private Map<Long, MiniNode> nodes = new HashMap<Long, MiniNode>();

    /** the ways in the OSM file. */
    private Map<Long, MiniWay> ways = new HashMap<Long, MiniWay>();

    /** the relations in the OSM file. */
    private Map<Long, MiniRelation> relations = new HashMap<Long, MiniRelation>();

    /** the key - value pairs to read. There can be multiple values per key, or '*' for all. */
    private final List<FeatureInterface> featuresToRead;

    /** an optional transformation of the lat/lon (or other) coordinates. */
    private final CoordinateTransform coordinateTransform;

    /**
     * Construct a sink to read the features form the OSM file. For OSM this the Feature list is typically the complete set of
     * features that needs to be read. It is rare that multiple OSM files are available for the data, but this could be the case
     * (e.g., state or country OSM files).<br>
     * TODO: add an optional initial extent in case the source's extent is much larger than the extent we want to display
     * @param featuresToRead the features that the sink needs to read.
     * @param coordinateTransform the transformation of (x, y) coordinates to (x', y') coordinates.
     */
    public OsmEntityProcessor(final List<FeatureInterface> featuresToRead, final CoordinateTransform coordinateTransform)
    {
        this.featuresToRead = featuresToRead;
        this.coordinateTransform = coordinateTransform;
    }

    /**
     * Process an entity that was just read.
     * @param entity the node, way or other OSM entity
     */
    public void process(final OsmEntity entity)
    {
        if (entity instanceof Node node)
        {
            MiniNode miniNode = new MiniNode(node.getId(), (float) node.getLat(), (float) node.getLon());
            this.nodes.put(miniNode.id, miniNode);
            /*-
            Iterator<Tag> tagIterator = entity.getTags().iterator();
            while (tagIterator.hasNext())
            {
                Tag tag = tagIterator.next();
                String key = tag.getKey();
                String value = tag.getValue();
                // TODO: look whether we want to display special nodes.
            }
            */
        }

        else if (entity instanceof Way way)
        {
            boolean read = false;
            FeatureInterface featureToUse = null;
            for (var entry : way.getTags().entrySet())
            {
                String key = entry.getKey();
                String value = entry.getValue();
                for (FeatureInterface feature : this.featuresToRead)
                {
                    if (feature.getKey().equals("*"))
                    {
                        featureToUse = feature;
                        read = true;
                        break;
                    }
                    if (feature.getKey().equals(key))
                    {
                        if (feature.getValue().equals("*") || feature.getValue().equals(value))
                        {
                            featureToUse = feature;
                            read = true;
                            break;
                        }
                    }
                }
                if (read)
                {
                    break;
                }
            }

            // store all the ways since they can be used in a Relation. featureToUse can therefore be null.
            List<MiniNode> wayNodes = new ArrayList<>();
            for (long nodeId : way.getRefs())
            {
                MiniNode node = this.nodes.get(nodeId);
                if (node == null)
                    continue;
                wayNodes.add(node);
            }
            MiniWay miniWay = new MiniWay(way.getId(), featureToUse, wayNodes);
            this.ways.put(miniWay.id, miniWay);
        }

        else if (entity instanceof Relation relation)
        {
            boolean read = false;
            FeatureInterface featureToUse = null;
            for (var entry : relation.getTags().entrySet())
            {
                String key = entry.getKey();
                String value = entry.getValue();
                for (FeatureInterface feature : this.featuresToRead)
                {
                    if (feature.getKey().equals("*"))
                    {
                        featureToUse = feature;
                        read = true;
                        break;
                    }
                    if (feature.getKey().equals(key))
                    {
                        if (feature.getValue().equals("*") || feature.getValue().equals(value))
                        {
                            featureToUse = feature;
                            read = true;
                            break;
                        }
                    }
                }
                if (read)
                {
                    break;
                }
            }

            if (read)
            {
                // For now, just multipolygon
                if ("multipolygon".equals(relation.getTags().get("type")) || "boundary".equals(relation.getTags().get("type")))
                {
                    List<Long> outerWayIds = new ArrayList<>();
                    List<Long> innerWayIds = new ArrayList<>();
                    for (Relation.Member member : relation.getMembers())
                    {
                        if (member.type() != Relation.Type.WAY)
                            continue;
                        long wayId = member.ref();
                        if (!this.ways.containsKey(wayId))
                        {
                            continue;
                        }
                        if ("inner".equals(member.role()))
                            innerWayIds.add(wayId);
                        else
                            outerWayIds.add(wayId);
                    }
                    MiniRelation miniRelation = new MiniRelation(relation.getId(), featureToUse, outerWayIds, innerWayIds,
                            relation.getTags().get("name"));
                    this.relations.put(miniRelation.id, miniRelation);
                }
            }
        }
    }

    /**
     * Preprocessing of the data.
     */
    public void initialize()
    {
        // Nothing to do right now
    }

    /**
     * Postprocessing of the data.
     */
    public void complete()
    {
        for (MiniWay way : this.ways.values())
        {
            if (way.feature != null) // can be null if only used in a relation
            {
                addWay(way);
            }
        }
        for (MiniRelation relation : this.relations.values())
        {
            addRelation(relation);
        }
        System.out.println("Nodes read: " + this.nodes.size() + ", ways read: " + this.ways.size() + ", relations read: "
                + this.relations.size());
    }

    /**
     * Add a way to a feature.
     * @param way the way to add to the feature shape list
     */
    private void addWay(final MiniWay way)
    {
        Path2D.Float path = new Path2D.Float(Path2D.WIND_NON_ZERO, way.wayNodesLat.length);
        boolean start = false;
        for (int i = 0; i < way.wayNodesLat.length; i++)
        {
            FloatXY coordinate;
            if (way.wayNodesId[i] != 0)
            {
                MiniNode node = this.nodes.get(way.wayNodesId[i]);
                coordinate = this.coordinateTransform.floatTransform(node.lon, node.lat);
            }
            else
            {
                coordinate = this.coordinateTransform.floatTransform(way.wayNodesLon[i], way.wayNodesLat[i]);
            }
            if (!start)
            {
                path.moveTo(coordinate.x(), coordinate.y());
                start = true;
            }
            path.lineTo(coordinate.x(), coordinate.y());
        }
        way.feature.addShape(path);
    }

    /**
     * Add a relation to a feature.
     * @param relation the relation to add to the feature shape list
     */
    private void addRelation(final MiniRelation relation)
    {
        List<Path2D> outerRings = makeRings(relation.outerWayIds, true);
        List<Path2D> innerRings = makeRings(relation.innerWayIds, false);
        Path2D.Float multipolygon = new Path2D.Float(Path2D.WIND_NON_ZERO);
        for (Path2D outer : outerRings)
            multipolygon.append(outer, false);
        for (Path2D inner : innerRings)
            multipolygon.append(inner, false);
        relation.feature.addShape(multipolygon);
    }

    /**
     * Make a list of inner or outer rings for the relation.
     * @param wayIds the inner or outer wayIds to process
     * @param outer to determine the winding
     * @return a list of Path2D elements for inner or outer ways
     */
    private List<Path2D> makeRings(final List<Long> wayIds, final boolean outer)
    {
        var rings = new ArrayList<Path2D>();
        var partials = new ArrayList<List<FloatXY>>();
        for (long wayId : wayIds)
        {
            MiniWay way = this.ways.get(wayId);
            boolean started = false;
            List<FloatXY> ring = new ArrayList<>();
            FloatXY first = null;
            FloatXY coordinate = null;
            for (int i = 0; i < way.wayNodesLat.length; i++)
            {
                if (way.wayNodesId[i] != 0)
                {
                    MiniNode node = this.nodes.get(way.wayNodesId[i]);
                    coordinate = this.coordinateTransform.floatTransform(node.lon, node.lat);
                }
                else
                {
                    coordinate = this.coordinateTransform.floatTransform(way.wayNodesLon[i], way.wayNodesLat[i]);
                }
                if (!started)
                {
                    first = coordinate;
                    started = true;
                }
                ring.add(coordinate);
            }

            // Sometimes rings are made from partial ways that still have to be stitched together
            if (!first.equals(coordinate))
            {
                partials.add(ring);
                continue;
            }

            Path2D path = makePath(first, ring, outer);
            rings.add(path);
        }
        if (!partials.isEmpty())
            stitch(partials, outer, rings);
        return rings;
    }

    /**
     * Test whether the ring is clockwise or anticlockwise.
     * @param ring list of points
     * @return clockwise or not
     */
    protected boolean isClockwise(final List<FloatXY> ring)
    {
        double sum = 0;
        for (int i = 0; i < ring.size() - 1; i++)
        {
            FloatXY p1 = ring.get(i);
            FloatXY p2 = ring.get(i + 1);
            sum += (p2.x() - p1.x()) * (p2.y() + p1.y());
        }
        return sum > 0;
    }

    /**
     * Make a path from a ring of coordinates.
     * @param first the first point
     * @param ring the list of points
     * @param outer outer ring or inner ring
     * @return the path constructed from the points
     */
    protected Path2D makePath(final FloatXY first, final List<FloatXY> ring, final boolean outer)
    {
        // Outer rings have to be clockwise; inner rings have to be anti-clockwise
        if (outer)
        {
            if (!isClockwise(ring))
                Collections.reverse(ring);
        }
        else
        {
            if (isClockwise(ring))
                Collections.reverse(ring);
        }

        Path2D path = new Path2D.Float();
        path.moveTo(first.x(), first.y());
        boolean started = false;
        for (var c : ring)
        {
            if (!started)
            {
                path.moveTo(c.x(), c.y());
                started = true;
            }
            else
            {
                path.lineTo(c.x(), c.y());
            }
        }
        path.closePath();
        return path;
    }

    /**
     * Stitch partial rings together into rings if possible.
     * @param partials partial rings, i.e. the ways that make up one or more rings
     * @param outer outer or inner ring
     * @param rings the ring list to which stitched rings need to be added
     */
    protected void stitch(final List<List<FloatXY>> partials, final boolean outer, final List<Path2D> rings)
    {
        boolean changed;
        do
        {
            changed = false;

            outerLoop: for (int i = 0; i < partials.size(); i++)
            {
                List<FloatXY> p1 = partials.get(i);

                for (int j = 0; j < partials.size(); j++)
                {
                    if (i == j)
                        continue;

                    List<FloatXY> p2 = partials.get(j);

                    FloatXY p1Start = p1.get(0);
                    FloatXY p1End = p1.get(p1.size() - 1);
                    FloatXY p2Start = p2.get(0);
                    FloatXY p2End = p2.get(p2.size() - 1);

                    if (p1End.equals(p2Start))
                    {
                        // p1 ---> p2
                        p1.addAll(p2.subList(1, p2.size()));
                        partials.remove(j);
                        changed = true;
                        break outerLoop;
                    }
                    else if (p1End.equals(p2End))
                    {
                        // p1 ---> reversed(p2)
                        Collections.reverse(p2);
                        p1.addAll(p2.subList(1, p2.size()));
                        partials.remove(j);
                        changed = true;
                        break outerLoop;
                    }
                    else if (p1Start.equals(p2End))
                    {
                        // p2 --> p1
                        p2.addAll(p1.subList(1, p1.size()));
                        partials.remove(i);
                        changed = true;
                        break outerLoop;
                    }
                    else if (p1Start.equals(p2Start))
                    {
                        // reversed(p1) --> p2
                        Collections.reverse(p1);
                        p1.addAll(p2.subList(1, p2.size()));
                        partials.remove(j);
                        changed = true;
                        break outerLoop;
                    }
                }
            }
        }
        while (changed);

        // Convert closed rings to Path2D
        for (var p : partials)
        {
            if (p.size() > 2 && p.get(0).equals(p.get(p.size() - 1)))
            {
                Path2D path = makePath(p.get(0), p, outer);
                rings.add(path);
            }
        }
    }

    /**
     * Store only the id, lat and lon of a Node to reduce the memory footprint of the Node storage during parsing. <br>
     * <br>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    protected static class MiniNode
    {
        /** the node id. */
        protected long id;

        /** the latitude of the node. */
        protected float lat;

        /** the longitude of the node. */
        protected float lon;

        /**
         * Create a MniniNode.
         * @param id the node id
         * @param lat the latitude of the node
         * @param lon the longitude of the node
         */
        public MiniNode(final long id, final float lat, final float lon)
        {
            this.id = id;
            this.lat = lat;
            this.lon = lon;
        }
    }

    /**
     * Store the minimum set of features of a Way to reduce the memory footprint of the Way storage during parsing. <br>
     * <br>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    protected static class MiniWay
    {
        /** the way id. */
        protected long id;

        /** the feature that characterizes this way. */
        protected FeatureInterface feature;

        /** the latitude of the way nodes that define e.g. a contour. */
        protected float[] wayNodesLat;

        /** the longitude of the way nodes that define e.g. a contour. */
        protected float[] wayNodesLon;

        /** the possible nodeIds. */
        protected long[] wayNodesId;

        /**
         * Create a MniniWay.
         * @param id the way id
         * @param feature the feature that characterizes this way
         * @param wayNodes the way nodes
         */
        public MiniWay(final long id, final FeatureInterface feature, final List<MiniNode> wayNodes)
        {
            this.id = id;
            this.feature = feature;
            this.wayNodesLat = new float[wayNodes.size()];
            this.wayNodesLon = new float[wayNodes.size()];
            this.wayNodesId = new long[wayNodes.size()];
            int i = 0;
            for (MiniNode wayNode : wayNodes)
            {
                this.wayNodesLat[i] = (float) wayNode.lat;
                this.wayNodesLon[i] = (float) wayNode.lon;
                this.wayNodesId[i] = wayNode.id;
                i++;
            }
        }

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            return "MiniWay [id=" + this.id + ", feature=" + this.feature + ", wayNodesLat=" + Arrays.toString(this.wayNodesLat)
                    + ", wayNodesLon=" + Arrays.toString(this.wayNodesLon) + "]";
        }

    }

    /**
     * Store the minimum set of features of a Relation to reduce the memory footprint of the Relation storage during parsing.
     * <br>
     * <br>
     * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
     */
    protected static class MiniRelation
    {
        /** the relation id. */
        protected long id;

        /** the feature that characterizes this relation. */
        protected FeatureInterface feature;

        /** the way ids of the outer ways. */
        protected List<Long> outerWayIds;

        /** the way ids of the inner ways. */
        protected List<Long> innerWayIds;

        /** the name. */
        protected String name;

        /**
         * Create a MniniRelation.
         * @param id the relation id
         * @param feature the feature that characterizes this relation
         * @param outerWayIds the way ids of the outer ways
         * @param innerWayIds the way ids of the inner ways
         * @param name the name of the relation for debugging
         */
        public MiniRelation(final long id, final FeatureInterface feature, final List<Long> outerWayIds,
                final List<Long> innerWayIds, final String name)
        {
            this.id = id;
            this.feature = feature;
            this.outerWayIds = outerWayIds;
            this.innerWayIds = innerWayIds;
            this.name = name;
        }
    }

}
