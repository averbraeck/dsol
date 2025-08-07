package nl.tudelft.simulation.dsol.animation.gis.osm;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.InflaterInputStream;

import com.google.protobuf.ByteString;

import nl.tudelft.simulation.dsol.animation.gis.osm.pbf.OSMFileFormat;
import nl.tudelft.simulation.dsol.animation.gis.osm.pbf.OSMPBF;

/**
 * OsmPbfReader reads from an OSM PBF stream.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OsmPbfReader
{

    /**
     * Create a PBF reader.
     * @param inputFile the input file
     * @param processor the entity processor
     * @throws IOException on I/O error
     */
    public OsmPbfReader(final File inputFile, final OsmEntityProcessor processor) throws IOException
    {
        FileInputStream fis = new FileInputStream(inputFile.getAbsolutePath());
        DataInputStream dis = new DataInputStream(new BufferedInputStream(fis));

        while (dis.available() > 0)
        {
            int headerSize = dis.readInt();
            byte[] headerBytes = dis.readNBytes(headerSize);
            OSMFileFormat.BlobHeader header = OSMFileFormat.BlobHeader.parseFrom(headerBytes);

            byte[] blobBytes = dis.readNBytes(header.getDatasize());
            OSMFileFormat.Blob blob = OSMFileFormat.Blob.parseFrom(blobBytes);

            InputStream blobStream;
            if (blob.hasZlibData())
            {
                blobStream = new InflaterInputStream(new ByteArrayInputStream(blob.getZlibData().toByteArray()));
            }
            else if (blob.hasRaw())
            {
                blobStream = blob.getRaw().newInput();
            }
            else
            {
                continue;
            }

            if ("OSMData".equals(header.getType()))
            {
                OSMPBF.PrimitiveBlock block = OSMPBF.PrimitiveBlock.parseFrom(blobStream);
                parsePrimitiveBlock(block, processor);
            }
        }

        dis.close();
        fis.close();
    }

    /**
     * Parse a block of entities.
     * @param block the block
     * @param processor the processor to further process the entities
     */
    public static void parsePrimitiveBlock(final OSMPBF.PrimitiveBlock block, final OsmEntityProcessor processor)
    {
        long granularity = block.getGranularity();
        long latOffset = block.getLatOffset();
        long lonOffset = block.getLonOffset();
        List<ByteString> stringTable = block.getStringtable().getSList();

        for (OSMPBF.PrimitiveGroup group : block.getPrimitivegroupList())
        {
            if (group.hasDense())
            {
                long id = 0, lat = 0, lon = 0;
                for (int i = 0; i < group.getDense().getIdCount(); i++)
                {
                    id += group.getDense().getId(i);
                    lat += group.getDense().getLat(i);
                    lon += group.getDense().getLon(i);

                    double latDeg = 1e-9 * (latOffset + lat * granularity);
                    double lonDeg = 1e-9 * (lonOffset + lon * granularity);

                    Node node = new Node(id, latDeg, lonDeg);
                    processor.process(node);
                }
            }

            for (OSMPBF.Way way : group.getWaysList())
            {
                long wayId = way.getId();
                List<Long> refs = new ArrayList<>();
                long ref = 0;
                for (long delta : way.getRefsList())
                {
                    ref += delta;
                    refs.add(ref);
                }

                Map<String, String> tags = new HashMap<>();
                for (int i = 0; i < way.getKeysCount(); i++)
                {
                    String k = stringTable.get(way.getKeys(i)).toStringUtf8();
                    String v = stringTable.get(way.getVals(i)).toStringUtf8();
                    tags.put(k, v);
                }

                Way w = new Way(wayId, refs, tags);
                processor.process(w);
            }

            for (OSMPBF.Relation relation : group.getRelationsList())
            {
                long relationId = relation.getId();
                Relation rel = new Relation(relationId);
                long lastId = 0; // note that relation.getMemids(i) contains delta-encoded values, not absolute IDs.
                for (int i = 0; i < relation.getMemidsCount(); i++)
                {
                    long delta = relation.getMemids(i);
                    long ref = lastId + delta;
                    lastId = ref;
                    String role = stringTable.get(relation.getRolesSid(i)).toStringUtf8();
                    Relation.Type type = switch (relation.getTypes(i))
                    {
                        case NODE -> Relation.Type.NODE;
                        case WAY -> Relation.Type.WAY;
                        case RELATION -> Relation.Type.RELATION;
                    };
                    Relation.Member member = new Relation.Member(type, ref, role);
                    rel.addMember(member);
                }
                for (int i = 0; i < relation.getKeysCount(); i++)
                {
                    String k = stringTable.get(relation.getKeys(i)).toStringUtf8();
                    String v = stringTable.get(relation.getVals(i)).toStringUtf8();
                    rel.addTag(k, v);
                }
                processor.process(rel);
            }
        }
    }
}
