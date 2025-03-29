package nl.tudelft.simulation.dsol.animation.gis.osm;

import java.awt.geom.Path2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import nl.tudelft.simulation.dsol.animation.gis.FeatureInterface;
import nl.tudelft.simulation.dsol.animation.gis.FloatXY;
import nl.tudelft.simulation.dsol.animation.gis.GisObject;
import nl.tudelft.simulation.dsol.animation.gis.SerializablePath;
import nl.tudelft.simulation.dsol.animation.gis.transform.CoordinateTransform;

/**
 * OsmLayerSink.java.
 * <p>
 * Copyright (c) 2021-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class OsmLayerSink implements Sink
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
     * TODO: add an optional initial extent in case the sounrce's extent is much larger than the extent we want to display
     * @param featuresToRead the features that the sink needs to read.
     * @param coordinateTransform the transformation of (x, y) coordinates to (x', y') coordinates.
     */
    public OsmLayerSink(final List<FeatureInterface> featuresToRead, final CoordinateTransform coordinateTransform)
    {
        this.featuresToRead = featuresToRead;
        this.coordinateTransform = coordinateTransform;
    }

    @Override
    public void process(final EntityContainer entityContainer)
    {
        Entity entity = entityContainer.getEntity();

        if (entity instanceof Node)
        {
            Node node = (Node) entity;
            MiniNode miniNode = new MiniNode(node.getId(), (float) node.getLatitude(), (float) node.getLongitude());
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

        else if (entity instanceof Way)
        {
            boolean read = false;
            Iterator<Tag> tagIterator = entity.getTags().iterator();
            FeatureInterface featureToUse = null;
            while (tagIterator.hasNext())
            {
                Tag tag = tagIterator.next();
                String key = tag.getKey();
                String value = tag.getValue();
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
                { break; }
            }
            if (read)
            {
                Way way = (Way) entity;
                MiniWay miniWay = new MiniWay(way.getId(), featureToUse, way.getWayNodes());
                this.ways.put(miniWay.id, miniWay);
            }
        }

        else if (entity instanceof Relation)
        {
            Iterator<Tag> tagint = entity.getTags().iterator();
            while (tagint.hasNext())
            {
                Tag route = tagint.next();
            }
        }

    }

    @Override
    public void initialize(final Map<String, Object> metaData)
    {
        // nothing to do right now.
    }

    @Override
    public void complete()
    {
        for (MiniWay way : this.ways.values())
        {
            addWay(way);
        }
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

    @Override
    public void close()
    {
        // nothing to do right now.
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
        public MiniWay(final long id, final FeatureInterface feature, final Collection<WayNode> wayNodes)
        {
            this.id = id;
            this.feature = feature;
            this.wayNodesLat = new float[wayNodes.size()];
            this.wayNodesLon = new float[wayNodes.size()];
            this.wayNodesId = new long[wayNodes.size()];
            int i = 0;
            for (WayNode wayNode : wayNodes)
            {
                this.wayNodesLat[i] = (float) wayNode.getLatitude();
                this.wayNodesLon[i] = (float) wayNode.getLongitude();
                this.wayNodesId[i] = wayNode.getNodeId();
                i++;
            }
        }

    }
}
