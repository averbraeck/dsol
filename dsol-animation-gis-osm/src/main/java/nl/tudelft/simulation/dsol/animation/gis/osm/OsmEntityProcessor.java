package nl.tudelft.simulation.dsol.animation.gis.osm;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.tudelft.simulation.dsol.animation.gis.FeatureInterface;
import nl.tudelft.simulation.dsol.animation.gis.FloatXY;
import nl.tudelft.simulation.dsol.animation.gis.GisObject;
import nl.tudelft.simulation.dsol.animation.gis.SerializablePath;
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
    /** the ways in the OSM file. */
    private Map<Long, MiniWay> ways = new HashMap<Long, MiniWay>();

    /** the nodes in the OSM file. */
    private Map<Long, MiniNode> nodes = new HashMap<Long, MiniNode>();

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
            if (read)
            {
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
        }

        /*-
        else if (entity instanceof Relation)
        {
            Iterator<Tag> tagint = entity.getTags().iterator();
            while (tagint.hasNext())
            {
                Tag route = tagint.next();
            }
        }
        */

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
            addWay(way);
        }
        System.out.println("Nodes read: " + this.nodes.size() + ", ways read: " + this.ways.size());
    }

    /**
     * Add a way to a feature.
     * @param way the way to add to the feature shape list
     */
    private void addWay(final MiniWay way)
    {
        SerializablePath path = new SerializablePath(Path2D.WIND_NON_ZERO, way.wayNodesLat.length);
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
        String[] att = new String[0];
        way.feature.getShapes().add(new GisObject(path, att));
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
    }
}
