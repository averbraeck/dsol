package nl.tudelft.simulation.dsol.animation.gis.io;

/**
 * Endianness of the data; either big endian (Java, network), or little endian (Intel).
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public enum Endianness
{
    /** BIG ENDIAN. Aka network byte order. MSB first. */
    BIG_ENDIAN,

    /** LITTLE ENDIAN. Default in the Intel processors. LSB first. */
    LITTLE_ENDIAN;
}
