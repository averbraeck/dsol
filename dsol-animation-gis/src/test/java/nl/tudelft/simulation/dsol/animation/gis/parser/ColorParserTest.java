package nl.tudelft.simulation.dsol.animation.gis.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.awt.Color;

import org.djutils.exceptions.Try;
import org.junit.Test;

/**
 * ColorParserTest tests the ColorParser class.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ColorParserTest
{
    /** Test the ColorParser class. */
    @Test
    public void testColorParser()
    {
        assertNull(ColorParser.parse(""));
        assertNull(ColorParser.parse("  "));
        assertNull(ColorParser.parse("null"));
        assertNull(ColorParser.parse(" null  "));

        // without spaces
        Color c = ColorParser.parse("rgb(10,20,30)");
        assertEquals(10, c.getRed());
        assertEquals(20, c.getGreen());
        assertEquals(30, c.getBlue());

        c = ColorParser.parse("RGB(10,20,30)");
        assertEquals(10, c.getRed());
        assertEquals(20, c.getGreen());
        assertEquals(30, c.getBlue());

        c = ColorParser.parse("rgba(10,20,30,255)");
        assertEquals(10, c.getRed());
        assertEquals(20, c.getGreen());
        assertEquals(30, c.getBlue());
        assertEquals(255, c.getAlpha());

        c = ColorParser.parse("RGBA(10,20,30,255)");
        assertEquals(10, c.getRed());
        assertEquals(20, c.getGreen());
        assertEquals(30, c.getBlue());
        assertEquals(255, c.getAlpha());

        c = ColorParser.parse("#01070F");
        assertEquals(1, c.getRed());
        assertEquals(7, c.getGreen());
        assertEquals(15, c.getBlue());

        c = ColorParser.parse("#01070FFF");
        assertEquals(1, c.getRed());
        assertEquals(7, c.getGreen());
        assertEquals(15, c.getBlue());
        assertEquals(255, c.getAlpha());

        // with some whitespaces
        c = ColorParser.parse("rgb( 10, 20, 30 )");
        assertEquals(10, c.getRed());
        assertEquals(20, c.getGreen());
        assertEquals(30, c.getBlue());

        c = ColorParser.parse("  rgba(10,20,    30,    255)");
        assertEquals(10, c.getRed());
        assertEquals(20, c.getGreen());
        assertEquals(30, c.getBlue());
        assertEquals(255, c.getAlpha());

        c = ColorParser.parse("  #01070F    ");
        assertEquals(1, c.getRed());
        assertEquals(7, c.getGreen());
        assertEquals(15, c.getBlue());

        c = ColorParser.parse(" #01070FFF");
        assertEquals(1, c.getRed());
        assertEquals(7, c.getGreen());
        assertEquals(15, c.getBlue());
        assertEquals(255, c.getAlpha());

        c = ColorParser.parse("#+1+2+3"); // this works, but is of course not so elegant...
        assertEquals(1, c.getRed());
        assertEquals(2, c.getGreen());
        assertEquals(3, c.getBlue());
    }

    /** Test the ColorParser class for RGB parser exceptions. */
    @Test
    public void testColorParserRGBExceptions()
    {
        // wrong number of arguments
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                ColorParser.parse("rgb(10,20)");
            }
        }, IllegalArgumentException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                ColorParser.parse("rgb(10,20,30");
            }
        }, IllegalArgumentException.class);
        
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                ColorParser.parse("rgb(10,20,30,40)");
            }
        }, IllegalArgumentException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                ColorParser.parse("rgba(10,20,30,40");
            }
        }, IllegalArgumentException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                ColorParser.parse("rgba(10,20,30)");
            }
        }, IllegalArgumentException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                ColorParser.parse("rgba(10,20,30,40,50)");
            }
        }, IllegalArgumentException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                ColorParser.parse("rgb(10,20,300)");
            }
        }, IllegalArgumentException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                ColorParser.parse("rga(10,20,30)");
            }
        }, IllegalArgumentException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                ColorParser.parse("rgb(10,20,oo)");
            }
        }, NumberFormatException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                ColorParser.parse("rgba(r,g,b,a)");
            }
        }, NumberFormatException.class);

    }


    /** Test the ColorParser class for hex parser exceptions. */
    @Test
    public void testColorParserHexExceptions()
    {
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                ColorParser.parse("FFFFFF");
            }
        }, IllegalArgumentException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                ColorParser.parse("#FFFGFF");
            }
        }, NumberFormatException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                ColorParser.parse("#0102030405");
            }
        }, IllegalArgumentException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                ColorParser.parse("#01020");
            }
        }, IllegalArgumentException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                ColorParser.parse("#-12233");
            }
        }, IllegalArgumentException.class);

    }
}
