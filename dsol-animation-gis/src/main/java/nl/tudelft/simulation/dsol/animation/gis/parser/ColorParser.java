package nl.tudelft.simulation.dsol.animation.gis.parser;

import java.awt.Color;

/**
 * ColorParser parses a string that contains a color as rgb(r,g,b), rgba(r,g,b,a), or hex #rrggbb or #rrggbbaa.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class ColorParser
{
    /** */
    private ColorParser()
    {
        // Utility class
    }

    /**
     * Parse a string that contains a color as rgb(r,g,b), rgba(r,g,b,a), or hex #rrggbb or #rrggbbaa. When the string is empty
     * or contains "null", null is returned.
     * @param colorString String; the textual representation of the color
     * @return Color; the parsed color, or null in casethe input was "null" or empty
     * @throws IllegalArgumentException when the text representation is not valid
     * @throws IllegalArgumentException if r, g, b or a are outside of the range0 to 255, inclusive
     * @throws NumberFormatException when one of the r, g, b, or a values could not be converted from string to int
     */
    public static Color parse(final String colorString) throws IllegalArgumentException
    {
        String lc = colorString.toLowerCase().trim();
        if (lc.equals("null") || lc.length() == 0)
        {
            return null;
        }
        if (lc.startsWith("rgba(") && lc.endsWith(")"))
        {
            String[] rgba = lc.substring(5, lc.length() - 1).split(",");
            if (rgba.length == 4)
            {
                int r = Integer.parseInt(rgba[0].trim());
                int g = Integer.parseInt(rgba[1].trim());
                int b = Integer.parseInt(rgba[2].trim());
                int a = Integer.parseInt(rgba[3].trim());
                return new Color(r, g, b, a);
            }
        }
        else if (lc.startsWith("rgb(") && lc.endsWith(")"))
        {
            String[] rgb = lc.substring(4, lc.length() - 1).split(",");
            if (rgb.length == 3)
            {
                int r = Integer.parseInt(rgb[0].trim());
                int g = Integer.parseInt(rgb[1].trim());
                int b = Integer.parseInt(rgb[2].trim());
                return new Color(r, g, b);
            }
        }
        else if (lc.startsWith("#"))
        {
            if (lc.length() == 7)
            {
                int r = Integer.parseInt(lc.substring(1, 3).trim(), 16);
                int g = Integer.parseInt(lc.substring(3, 5).trim(), 16);
                int b = Integer.parseInt(lc.substring(5, 7).trim(), 16);
                return new Color(r, g, b);
            }
            else if (lc.length() == 9)
            {
                int r = Integer.parseInt(lc.substring(1, 3).trim(), 16);
                int g = Integer.parseInt(lc.substring(3, 5).trim(), 16);
                int b = Integer.parseInt(lc.substring(5, 7).trim(), 16);
                int a = Integer.parseInt(lc.substring(7, 9).trim(), 16);
                return new Color(r, g, b, a);
            }
        }
        throw new IllegalArgumentException("Trying to parse illegal color string " + colorString);
    }
}
