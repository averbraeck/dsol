package nl.tudelft.simulation.dsol.animation.gis.osm;

/**
 * OsmFormat indicates the file format to read.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public enum OsmFormat
{
    /** Protocol buffer, binary. */
    PBF,
    
    /** OSM, XML. */
    OSM,
    
    /** Compressed XML with GZIP. */
    GZIP,
    
    /** Compressed XML with BZIP2. */
    BZIP2;
}
