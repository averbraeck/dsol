package nl.tudelft.simulation.dsol.animation.gis;

/**
 * MapUnits indicates the units for a map. If two coordinates are "1" apart, what does that "1" stand for? 
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public enum MapUnits
{
    /** FEET constant. */
    FEET,

    /** INCHES constant. */
    INCHES,

    /** KILOMETERS constant. */
    KILOMETERS,

    /** METERS constant. */
    METERS,

    /** MILES constant. */
    MILES,
    
    /** DEGREES constant, e.g., for WGS84. */
    DEGREES,

    /** DECIMAL DEGREES constant. A decimal degree is 1/100 of the earth circumference. */
    DECIMAL_DEGREES;

}
