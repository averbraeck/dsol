package nl.tudelft.simulation.dsol.model.inputparameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.SortedMap;
import java.util.TreeMap;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.unit.SpeedUnit;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vfloat.scalar.FloatLength;
import org.djutils.exceptions.Try;
import org.djutils.reflection.ClassUtil;
import org.junit.Test;

/**
 * InputParameterTest.java. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InputParameterTest
{
    /**
     * Test boolean input parameter, and the generic methods of the AbstractInputParameter, so they don't have to be tested by
     * all the other types of input parameters.
     * @throws InputParameterException on setting error
     */
    @Test
    public void testInputParameterBoolean() throws InputParameterException
    {
        InputParameterBoolean ip = new InputParameterBoolean("bool", "boolean", "boolean value", true, 1.0);
        assertEquals("bool", ip.getKey());
        assertEquals("bool", ip.getExtendedKey());
        assertEquals("boolean", ip.getShortName());
        assertEquals("boolean value", ip.getDescription());
        assertEquals(true, ip.getDefaultValue());
        assertEquals(1.0, ip.getDisplayPriority(), 1E-6);
        assertEquals(true, ip.getCalculatedValue());
        assertEquals(true, ip.getValue());
        InputParameterMap map = makeInputParameterMap();
        map.add(ip);
        assertEquals("bool", ip.getKey());
        assertEquals("map.bool", ip.getExtendedKey());
        assertEquals("bool[boolean] = true", ip.toString());

        ip.setValue(false);
        assertEquals(true, ip.getDefaultValue());
        assertEquals(false, ip.getCalculatedValue());
        assertEquals(false, ip.getValue());

        ip.setBooleanValue(true);
        assertEquals(true, ip.getDefaultValue());
        assertEquals(true, ip.getCalculatedValue());
        assertEquals(true, ip.getValue());

        assertFalse(ip.isReadOnly());
        ip.setReadOnly(true);
        assertTrue(ip.isReadOnly());
        Try.testFail(() -> { ip.setValue(false); });

        ip.setDefaultValue(false);
        assertEquals(false, ip.getDefaultValue());
        assertEquals(true, ip.getCalculatedValue());
        assertEquals(true, ip.getValue());

        // clone
        InputParameterBoolean ipClone = ip.clone();
        assertFalse(ip == ipClone);
        assertEquals("bool", ipClone.getKey());
        assertEquals("map.bool", ipClone.getExtendedKey());
        assertEquals("boolean", ipClone.getShortName());
        assertEquals("boolean value", ipClone.getDescription());
        assertEquals(false, ipClone.getDefaultValue());

        // null and NaN
        Try.testFail(() -> { new InputParameterBoolean(null, "y", "z", false, 2.0); });
        Try.testFail(() -> { new InputParameterBoolean("x", null, "z", false, 2.0); });
        Try.testFail(() -> { new InputParameterBoolean("x", "y", null, false, 2.0); });
        Try.testFail(() -> { new InputParameterBoolean("x", "y", "z", false, Double.NaN); });
        Try.testFail(() -> { new InputParameterString("x", "y", "z", null, 2.0); });
        InputParameterString ips = new InputParameterString("x", "y", "z", "xyz", 1.0);
        Try.testFail(() -> { ips.setDefaultValue(null); });
        Try.testFail(() -> { ips.setValue(null); });

        // no period in key
        Try.testFail(() -> { new InputParameterBoolean("x.y", "y", "z", true, 2.0); });
    }

    /**
     * @return InputParameterMap; a new map
     */
    private static InputParameterMap makeInputParameterMap()
    {
        InputParameterMap map = new InputParameterMap("map", "parametermap", "parameter map", 10.0);
        assertEquals("map", map.getKey());
        assertEquals("map", map.getExtendedKey());
        assertEquals("parametermap", map.getShortName());
        assertEquals("parameter map", map.getDescription());
        assertEquals(10.0, map.getDisplayPriority(), 1E-6);
        return map;
    }

    /**
     * test double input parameter.
     * @throws InputParameterException on setValue that did not work
     */
    @Test
    public void testInputParameterDouble() throws InputParameterException
    {
        InputParameterDouble ip = new InputParameterDouble("d", "double", "double value", 12.34, 2.0);
        InputParameterMap map = makeInputParameterMap();
        map.add(ip);
        assertEquals("d", ip.getKey());
        assertEquals("map.d", ip.getExtendedKey());
        assertEquals("double", ip.getShortName());
        assertEquals("double value", ip.getDescription());
        assertEquals(12.34, ip.getDefaultValue(), 1E-6);
        assertEquals(2.0, ip.getDisplayPriority(), 1E-6);
        assertEquals(12.34, ip.getCalculatedValue(), 1E-6);
        assertEquals(12.34, ip.getValue(), 1E-6);
        assertEquals(-Double.MAX_VALUE, ip.getMinimumValue(), 1E-6);
        assertEquals(Double.MAX_VALUE, ip.getMaximumValue(), 1E-6);

        for (boolean includeMin : new boolean[] {false, true})
        {
            for (boolean includeMax : new boolean[] {false, true})
            {
                InputParameterDouble ipmm = new InputParameterDouble("d", "double", "double value", 12.34, 0.0, 20.0,
                        includeMin, includeMax, "%6.2f", 3.0);
                assertEquals(0.0, ipmm.getMinimumValue(), 1E-6);
                assertEquals(20.0, ipmm.getMaximumValue(), 1E-6);
                assertEquals("%6.2f", ipmm.getFormat());
                assertEquals(includeMin, ipmm.isMinIncluded());
                assertEquals(includeMax, ipmm.isMaxIncluded());
                ipmm.setDoubleValue(0.0001);
                ipmm.setDoubleValue(19.999);
                if (includeMin)
                {
                    ipmm.setDoubleValue(0.0);
                    assertEquals(0.0, ipmm.getValue(), 1E-6);
                }
                else
                {
                    Try.testFail(() -> { ipmm.setDoubleValue(0.0); });
                }
                if (includeMax)
                {
                    ipmm.setDoubleValue(20.0);
                    assertEquals(20.0, ipmm.getValue(), 1E-6);
                }
                else
                {
                    Try.testFail(() -> { ipmm.setDoubleValue(20.0); });
                }
                Try.testFail(() -> { ipmm.setDoubleValue(Double.NaN); });
                Try.testFail(() -> { ipmm.setDoubleValue(Double.POSITIVE_INFINITY); });
                Try.testFail(() -> { ipmm.setDoubleValue(Double.NEGATIVE_INFINITY); });
                Try.testFail(() -> { ipmm.setDoubleValue(-50.0); });
                Try.testFail(() -> { ipmm.setDoubleValue(50.0); });

                ipmm.setFormat("%8.4f");
                assertEquals("%8.4f", ipmm.getFormat());
                ipmm.setMinIncluded(!includeMin);
                assertEquals(!includeMin, ipmm.isMinIncluded());
                ipmm.setMaxIncluded(!includeMax);
                assertEquals(!includeMax, ipmm.isMaxIncluded());
                ipmm.setMinimumValue(-100.0);
                assertEquals(-100.0, ipmm.getMinimumValue(), 1E-6);
                ipmm.setMaximumValue(100.0);
                assertEquals(100.0, ipmm.getMaximumValue(), 1E-6);
                ipmm.setValue(-50.0);
                ipmm.setValue(50.0);
                Try.testFail(() -> { ipmm.setDoubleValue(-150.0); });
                Try.testFail(() -> { ipmm.setDoubleValue(150.0); });
            }
        }

        Try.testFail(() -> { ip.setFormat(null); });
        Try.testFail(() -> { new InputParameterDouble("d", "d", "d", 12.34, 0.0, 20.0, false, false, null, 3.0); });
        InputParameterDouble ipClone = ip.clone();
        assertFalse(ip == ipClone);
        assertEquals(ip.getCalculatedValue(), ipClone.getCalculatedValue(), 1E-6);
    }

    /**
     * test float input parameter.
     * @throws InputParameterException on setValue that did not work
     */
    @Test
    public void testInputParameterFloat() throws InputParameterException
    {
        InputParameterFloat ip = new InputParameterFloat("f", "float", "float value", 12.34f, 2.0);
        InputParameterMap map = makeInputParameterMap();
        map.add(ip);
        assertEquals("f", ip.getKey());
        assertEquals("map.f", ip.getExtendedKey());
        assertEquals("float", ip.getShortName());
        assertEquals("float value", ip.getDescription());
        assertEquals(12.34, ip.getDefaultValue(), 1E-6);
        assertEquals(2.0, ip.getDisplayPriority(), 1E-6);
        assertEquals(12.34, ip.getCalculatedValue(), 1E-6);
        assertEquals(12.34, ip.getValue(), 1E-6);
        assertEquals(-Float.MAX_VALUE, ip.getMinimumValue(), 1E-6);
        assertEquals(Float.MAX_VALUE, ip.getMaximumValue(), 1E-6);

        for (boolean includeMin : new boolean[] {false, true})
        {
            for (boolean includeMax : new boolean[] {false, true})
            {
                InputParameterFloat ipmm = new InputParameterFloat("f", "float", "float value", 12.34f, 0.0f, 20.0f, includeMin,
                        includeMax, "%6.2f", 3.0);
                assertEquals(0.0f, ipmm.getMinimumValue(), 1E-6);
                assertEquals(20.0f, ipmm.getMaximumValue(), 1E-6);
                assertEquals("%6.2f", ipmm.getFormat());
                assertEquals(includeMin, ipmm.isMinIncluded());
                assertEquals(includeMax, ipmm.isMaxIncluded());
                ipmm.setFloatValue(0.0001f);
                ipmm.setFloatValue(19.999f);
                if (includeMin)
                {
                    ipmm.setFloatValue(0.0f);
                    assertEquals(0.0, ipmm.getValue(), 1E-6);
                }
                else
                {
                    Try.testFail(() -> { ipmm.setFloatValue(0.0f); });
                }
                if (includeMax)
                {
                    ipmm.setFloatValue(20.0f);
                    assertEquals(20.0, ipmm.getValue(), 1E-6);
                }
                else
                {
                    Try.testFail(() -> { ipmm.setFloatValue(20.0f); });
                }
                Try.testFail(() -> { ipmm.setFloatValue(Float.NaN); });
                Try.testFail(() -> { ipmm.setFloatValue(Float.POSITIVE_INFINITY); });
                Try.testFail(() -> { ipmm.setFloatValue(Float.NEGATIVE_INFINITY); });
                Try.testFail(() -> { ipmm.setFloatValue(-50.0f); });
                Try.testFail(() -> { ipmm.setFloatValue(50.0f); });

                ipmm.setFormat("%8.4f");
                assertEquals("%8.4f", ipmm.getFormat());
                ipmm.setMinIncluded(!includeMin);
                assertEquals(!includeMin, ipmm.isMinIncluded());
                ipmm.setMaxIncluded(!includeMax);
                assertEquals(!includeMax, ipmm.isMaxIncluded());
                ipmm.setMinimumValue(-100.0f);
                assertEquals(-100.0, ipmm.getMinimumValue(), 1E-6);
                ipmm.setMaximumValue(100.0f);
                assertEquals(100.0, ipmm.getMaximumValue(), 1E-6);
                ipmm.setValue(-50.0f);
                ipmm.setValue(50.0f);
                Try.testFail(() -> { ipmm.setFloatValue(-150.0f); });
                Try.testFail(() -> { ipmm.setFloatValue(150.0f); });
            }
        }

        Try.testFail(() -> { ip.setFormat(null); });
        Try.testFail(() -> { new InputParameterFloat("f", "f", "f", 12.34f, 0.0f, 20.0f, false, false, null, 3.0); });
        InputParameterFloat ipClone = ip.clone();
        assertFalse(ip == ipClone);
        assertEquals(ip.getCalculatedValue(), ipClone.getCalculatedValue(), 1E-6);
    }

    /**
     * test int input parameter.
     * @throws InputParameterException on setValue that did not work
     */
    @Test
    public void testInputParameterInteger() throws InputParameterException
    {
        InputParameterInteger ip = new InputParameterInteger("i", "int", "int value", 12, 2.0);
        InputParameterMap map = makeInputParameterMap();
        map.add(ip);
        assertEquals("i", ip.getKey());
        assertEquals("map.i", ip.getExtendedKey());
        assertEquals("int", ip.getShortName());
        assertEquals("int value", ip.getDescription());
        assertEquals(12, ip.getDefaultValue().intValue());
        assertEquals(2.0, ip.getDisplayPriority(), 1E-6);
        assertEquals(12, ip.getCalculatedValue().intValue());
        assertEquals(12, ip.getValue().intValue());
        assertEquals(-Integer.MAX_VALUE, ip.getMinimumValue().intValue());
        assertEquals(Integer.MAX_VALUE, ip.getMaximumValue().intValue());

        InputParameterInteger ipmm = new InputParameterInteger("i", "int", "int value", 12, 0, 20, "%6d", 3.0);
        assertEquals(0, ipmm.getMinimumValue().intValue());
        assertEquals(20, ipmm.getMaximumValue().intValue());
        assertEquals("%6d", ipmm.getFormat());
        ipmm.setIntValue(0);
        ipmm.setIntValue(20);
        Try.testFail(() -> { ipmm.setIntValue(-50); });
        Try.testFail(() -> { ipmm.setIntValue(50); });

        ipmm.setFormat("%8d");
        assertEquals("%8d", ipmm.getFormat());
        ipmm.setMinimumValue(-100);
        assertEquals(-100, ipmm.getMinimumValue().intValue());
        ipmm.setMaximumValue(100);
        assertEquals(100, ipmm.getMaximumValue().intValue());
        ipmm.setValue(-50);
        ipmm.setValue(50);
        Try.testFail(() -> { ipmm.setIntValue(-150); });
        Try.testFail(() -> { ipmm.setIntValue(150); });

        Try.testFail(() -> { ip.setFormat(null); });
        Try.testFail(() -> { new InputParameterInteger("i", "i", "i", 12, 0, 20, null, 3.0); });
        InputParameterInteger ipClone = ip.clone();
        assertFalse(ip == ipClone);
        assertEquals(ip.getCalculatedValue().intValue(), ipClone.getCalculatedValue().intValue());
    }

    /**
     * test long input parameter.
     * @throws InputParameterException on setValue that did not work
     */
    @Test
    public void testInputParameterLong() throws InputParameterException
    {
        InputParameterLong ip = new InputParameterLong("L", "long", "long value", 12L, 2.0);
        InputParameterMap map = makeInputParameterMap();
        map.add(ip);
        assertEquals("L", ip.getKey());
        assertEquals("map.L", ip.getExtendedKey());
        assertEquals("long", ip.getShortName());
        assertEquals("long value", ip.getDescription());
        assertEquals(12L, ip.getDefaultValue().longValue());
        assertEquals(2.0, ip.getDisplayPriority(), 1E-6);
        assertEquals(12L, ip.getCalculatedValue().longValue());
        assertEquals(12L, ip.getValue().longValue());
        assertEquals(-Long.MAX_VALUE, ip.getMinimumValue().longValue());
        assertEquals(Long.MAX_VALUE, ip.getMaximumValue().longValue());

        InputParameterLong ipmm = new InputParameterLong("L", "long", "long value", 12L, 0L, 20L, "%6d", 3.0);
        assertEquals(0L, ipmm.getMinimumValue().longValue());
        assertEquals(20L, ipmm.getMaximumValue().longValue());
        assertEquals("%6d", ipmm.getFormat());
        ipmm.setLongValue(0L);
        ipmm.setLongValue(20L);
        Try.testFail(() -> { ipmm.setLongValue(-50L); });
        Try.testFail(() -> { ipmm.setLongValue(50L); });

        ipmm.setFormat("%8d");
        assertEquals("%8d", ipmm.getFormat());
        ipmm.setMinimumValue(-100L);
        assertEquals(-100L, ipmm.getMinimumValue().longValue());
        ipmm.setMaximumValue(100L);
        assertEquals(100L, ipmm.getMaximumValue().longValue());
        ipmm.setValue(-50L);
        ipmm.setValue(50L);
        Try.testFail(() -> { ipmm.setLongValue(-150L); });
        Try.testFail(() -> { ipmm.setLongValue(150); });

        Try.testFail(() -> { ip.setFormat(null); });
        Try.testFail(() -> { new InputParameterLong("L", "L", "L", 12L, 0L, 20L, null, 3.0); });
        InputParameterLong ipClone = ip.clone();
        assertFalse(ip == ipClone);
        assertEquals(ip.getCalculatedValue().longValue(), ipClone.getCalculatedValue().longValue());
    }

    /**
     * test String input parameter.
     * @throws InputParameterException on setValue that did not work
     */
    @Test
    public void testInputParameterString() throws InputParameterException
    {
        InputParameterString ip = new InputParameterString("s", "string", "string value", "abc", 2.0);
        InputParameterMap map = makeInputParameterMap();
        map.add(ip);
        assertEquals("s", ip.getKey());
        assertEquals("map.s", ip.getExtendedKey());
        assertEquals("string", ip.getShortName());
        assertEquals("string value", ip.getDescription());
        assertEquals(2.0, ip.getDisplayPriority(), 1E-6);
        assertEquals("abc", ip.getDefaultValue());
        assertEquals("abc", ip.getCalculatedValue());
        assertEquals("abc", ip.getValue());
        ip.setValue("xyz");
        assertEquals("abc", ip.getDefaultValue());
        assertEquals("xyz", ip.getCalculatedValue());
        assertEquals("xyz", ip.getValue());
        ip.setStringValue("def");
        assertEquals("abc", ip.getDefaultValue());
        assertEquals("def", ip.getCalculatedValue());
        assertEquals("def", ip.getValue());
        Try.testFail(() -> { ip.setValue(null); });
        Try.testFail(() -> { ip.setStringValue(null); });
        Try.testFail(() -> { ip.setDefaultValue(null); });
        InputParameterString ipClone = ip.clone();
        assertFalse(ip == ipClone);
        assertEquals(ip.getCalculatedValue(), ipClone.getCalculatedValue());

        Try.testFail(() -> { new InputParameterString("s", "string", "string value", null, 2.0); });
    }

    /**
     * test DoubleScalar input parameter.
     * @throws InputParameterException on setValue that did not work
     */
    @Test
    @SuppressWarnings("checkstyle:methodlength")
    public void testInputParameterDoubleScalar() throws InputParameterException
    {
        InputParameterDoubleScalar<LengthUnit, Length> ip = new InputParameterDoubleScalar<>("len", "length", "length value",
                new Length(12.34, LengthUnit.CENTIMETER), 2.0);
        InputParameterMap map = makeInputParameterMap();
        map.add(ip);
        assertEquals("len", ip.getKey());
        assertEquals("map.len", ip.getExtendedKey());
        assertEquals("length", ip.getShortName());
        assertEquals("length value", ip.getDescription());
        assertEquals(2.0, ip.getDisplayPriority(), 1E-6);
        assertEquals(12.34, ip.getDoubleParameter().getDefaultValue(), 1E-6);
        assertEquals(LengthUnit.CENTIMETER, ip.getUnitParameter().getDefaultValue());
        ip.setCalculatedValue();
        assertEquals(new Length(12.34, LengthUnit.CENTIMETER), ip.getCalculatedValue());
        assertEquals(new Length(12.34, LengthUnit.CENTIMETER), ip.getDefaultTypedValue());
        assertEquals(12.34, ip.getDoubleParameter().getValue(), 1E-6);
        assertEquals(LengthUnit.CENTIMETER, ip.getUnitParameter().getValue());
        assertEquals(-Double.MAX_VALUE, ip.getMinimumValueSI(), 1E-6);
        assertEquals(Double.MAX_VALUE, ip.getMaximumValueSI(), 1E-6);
        ip.getDoubleParameter().setDoubleValue(23.45);
        ip.getUnitParameter().setMapValue(LengthUnit.KILOMETER);
        ip.setCalculatedValue();
        assertEquals(12.34, ip.getDoubleParameter().getDefaultValue(), 1E-6);
        assertEquals(LengthUnit.CENTIMETER, ip.getUnitParameter().getDefaultValue());
        assertEquals(new Length(12.34, LengthUnit.CENTIMETER), ip.getDefaultTypedValue());
        assertEquals(new Length(23.45, LengthUnit.KILOMETER), ip.getCalculatedValue());
        assertEquals(23.45, ip.getDoubleParameter().getValue(), 1E-6);
        assertEquals(LengthUnit.KILOMETER, ip.getUnitParameter().getValue());

        for (boolean includeMin : new boolean[] {false, true})
        {
            for (boolean includeMax : new boolean[] {false, true})
            {
                InputParameterDoubleScalar<LengthUnit,
                        Length> ipmm = new InputParameterDoubleScalar<>("len", "length", "length value",
                                new Length(12.34, LengthUnit.CENTIMETER), new Length(0.0, LengthUnit.CENTIMETER),
                                new Length(20.0, LengthUnit.CENTIMETER), includeMin, includeMax, "%6.2f", 3.0);
                assertEquals(0.0, ipmm.getMinimumValueSI(), 1E-6);
                assertEquals(new Length(20.0, LengthUnit.CENTIMETER).si, ipmm.getMaximumValueSI(), 1E-6);
                assertEquals("%6.2f", ipmm.getDoubleParameter().getFormat());
                assertEquals(includeMin, ipmm.isMinIncluded());
                assertEquals(includeMax, ipmm.isMaxIncluded());
                ipmm.getDoubleParameter().setValue(1.0);
                ipmm.getUnitParameter().setValue(LengthUnit.NANOMETER);
                ipmm.setCalculatedValue();
                ipmm.getDoubleParameter().setValue(199.0);
                ipmm.getUnitParameter().setValue(LengthUnit.MILLIMETER);
                ipmm.setCalculatedValue();
                if (includeMin)
                {
                    ipmm.getDoubleParameter().setDoubleValue(0.0);
                    ipmm.getUnitParameter().setValue(LengthUnit.CENTIMETER);
                    assertEquals(0.0, ipmm.getDoubleParameter().getValue(), 1E-6);
                    ipmm.setCalculatedValue();
                    assertEquals(Length.ZERO, ipmm.getCalculatedValue());
                }
                else
                {
                    Try.testFail(() -> {
                        ipmm.getDoubleParameter().setDoubleValue(0.0);
                        ipmm.getUnitParameter().setValue(LengthUnit.CENTIMETER);
                        ipmm.setCalculatedValue();
                    });
                }
                if (includeMax)
                {
                    ipmm.getDoubleParameter().setDoubleValue(20.0);
                    ipmm.getUnitParameter().setValue(LengthUnit.CENTIMETER);
                    assertEquals(20.0, ipmm.getDoubleParameter().getValue(), 1E-6);
                    ipmm.setCalculatedValue();
                    assertEquals(new Length(20.0, LengthUnit.CENTIMETER), ipmm.getCalculatedValue());
                }
                else
                {
                    Try.testFail(() -> {
                        ipmm.getDoubleParameter().setDoubleValue(20.0);
                        ipmm.getUnitParameter().setValue(LengthUnit.CENTIMETER);
                        ipmm.setCalculatedValue();
                    });
                }
                Try.testFail(() -> {
                    ipmm.getDoubleParameter().setDoubleValue(Double.NaN);
                    ipmm.getUnitParameter().setValue(LengthUnit.METER);
                    ipmm.setCalculatedValue();
                });
                Try.testFail(() -> {
                    ipmm.getDoubleParameter().setDoubleValue(Double.POSITIVE_INFINITY);
                    ipmm.getUnitParameter().setValue(LengthUnit.METER);
                    ipmm.setCalculatedValue();
                });
                Try.testFail(() -> {
                    ipmm.getDoubleParameter().setDoubleValue(Double.NEGATIVE_INFINITY);
                    ipmm.getUnitParameter().setValue(LengthUnit.METER);
                    ipmm.setCalculatedValue();
                });
                Try.testFail(() -> {
                    ipmm.getDoubleParameter().setDoubleValue(-50.0);
                    ipmm.getUnitParameter().setValue(LengthUnit.CENTIMETER);
                    ipmm.setCalculatedValue();
                });
                Try.testFail(() -> {
                    ipmm.getDoubleParameter().setDoubleValue(50.0);
                    ipmm.getUnitParameter().setValue(LengthUnit.CENTIMETER);
                    ipmm.setCalculatedValue();
                });

                ipmm.getDoubleParameter().setFormat("%8.4f");
                assertEquals("%8.4f", ipmm.getDoubleParameter().getFormat());
                ipmm.setMinIncluded(!includeMin);
                assertEquals(!includeMin, ipmm.isMinIncluded());
                ipmm.setMaxIncluded(!includeMax);
                assertEquals(!includeMax, ipmm.isMaxIncluded());

                // this is how to set boundaries
                ipmm.setMinimumValueSI(-100.0);
                assertEquals(-100.0, ipmm.getMinimumValueSI(), 1E-6);
                ipmm.setMaximumValueSI(100.0);
                assertEquals(100.0, ipmm.getMaximumValueSI(), 1E-6);
                ipmm.getDoubleParameter().setValue(-50.0);
                ipmm.getUnitParameter().setValue(LengthUnit.METER);
                ipmm.setCalculatedValue();
                ipmm.getDoubleParameter().setValue(50.0);
                ipmm.getUnitParameter().setValue(LengthUnit.METER);
                ipmm.setCalculatedValue();
                Try.testFail(() -> {
                    ipmm.getDoubleParameter().setDoubleValue(-150.0);
                    ipmm.getUnitParameter().setValue(LengthUnit.METER);
                    ipmm.setCalculatedValue();
                });
                Try.testFail(() -> {
                    ipmm.getDoubleParameter().setDoubleValue(150.0);
                    ipmm.getUnitParameter().setValue(LengthUnit.METER);
                    ipmm.setCalculatedValue();
                });

                // not the way it is supposed to work, but you can limit the double parameter separately
                ipmm.getDoubleParameter().setMinimumValue(-100.0);
                assertEquals(-100.0, ipmm.getDoubleParameter().getMinimumValue(), 1E-6);
                ipmm.getDoubleParameter().setMaximumValue(100.0);
                assertEquals(100.0, ipmm.getDoubleParameter().getMaximumValue(), 1E-6);
                ipmm.getDoubleParameter().setValue(-50.0);
                ipmm.getDoubleParameter().setValue(50.0);
                Try.testFail(() -> { ipmm.getDoubleParameter().setDoubleValue(-150.0); });
                Try.testFail(() -> { ipmm.getDoubleParameter().setDoubleValue(150.0); });
            }
        }

        Try.testFail(() -> { ip.getDoubleParameter().setFormat(null); });
        Try.testFail(() -> {
            new InputParameterDoubleScalar<LengthUnit, Length>("len", "len", "len", new Length(12.34, LengthUnit.CENTIMETER),
                    new Length(0.0, LengthUnit.CENTIMETER), new Length(20.0, LengthUnit.CENTIMETER), false, false, null, 3.0);
        });
        InputParameterDoubleScalar<LengthUnit, Length> ipClone = ip.clone();
        assertFalse(ip == ipClone);
        assertEquals(ip.getCalculatedValue(), ipClone.getCalculatedValue());

        InputParameterDoubleScalar<LengthUnit, Length> ipmmff = new InputParameterDoubleScalar<>("d", "double", "double value",
                new Length(12.34, LengthUnit.CENTIMETER), 0.0, 20.0, false, false, "%6.2f", 3.0);

        Try.testFail(() -> {
            ipmmff.getDoubleParameter().setDoubleValue(0.0);
            ipmmff.getUnitParameter().setMapValue(LengthUnit.HECTOMETER);
            ipmmff.setCalculatedValue();
        }, "Set value to 0.0 hm should have thrown exception");

        Try.testFail(() -> {
            ipmmff.getDoubleParameter().setDoubleValue(0.0);
            ipmmff.getUnitParameter().setMapValue(LengthUnit.METER);
            ipmmff.setCalculatedValue();
        }, "Set value to 20.0 m should have thrown exception");

        Try.testFail(() -> {
            ipmmff.getDoubleParameter().setDoubleValue(Double.NaN);
            ipmmff.getUnitParameter().setMapValue(LengthUnit.METER);
            ipmmff.setCalculatedValue();
        }, "Set value to NaN m should have thrown exception");

        Try.testFail(() -> {
            ipmmff.getDoubleParameter().setDoubleValue(Double.POSITIVE_INFINITY);
            ipmmff.getUnitParameter().setMapValue(LengthUnit.METER);
            ipmmff.setCalculatedValue();
        }, "Set value to Inf m should have thrown exception");

        Try.testFail(() -> {
            ipmmff.getDoubleParameter().setDoubleValue(50.0);
            ipmmff.getUnitParameter().setMapValue(LengthUnit.METER);
            ipmmff.setCalculatedValue();
        }, "Set value to 50 m should have thrown exception");

        Try.testFail(() -> {
            ipmmff.getDoubleParameter().setDoubleValue(1.0);
            ipmmff.getUnitParameter().setMapValue(LengthUnit.KILOMETER);
            ipmmff.setCalculatedValue();
        }, "Set value to 1.0 km should have thrown exception");

        ipmmff.getDoubleParameter().setDoubleValue(1.0);
        ipmmff.getUnitParameter().setMapValue(LengthUnit.METER);
        ipmmff.setCalculatedValue();

        ipmmff.getDoubleParameter().setDoubleValue(5000.0);
        ipmmff.getUnitParameter().setMapValue(LengthUnit.MILLIMETER);
        ipmmff.setCalculatedValue();

        // break the NaN barrier
        InputParameterDoubleScalar<LengthUnit, Length> ipBreak1 = new InputParameterDoubleScalar<>("len", "length",
                "length value", new Length(12.34, LengthUnit.CENTIMETER), 2.0);
        try
        {
            Field doubleField = ClassUtil.resolveField(ipBreak1.getDoubleParameter(), "value");
            doubleField.setAccessible(true);
            doubleField.set(ipBreak1.getDoubleParameter(), Double.NaN);
        }
        catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e)
        {
            fail(e.getMessage());
        }
        Try.testFail(() -> { ipBreak1.setCalculatedValue(); });

        // break the scalar constructor
        InputParameterDoubleScalar<LengthUnit, Length> ipBreak2 = new InputParameterDoubleScalar<>("len", "length",
                "length value", new Length(12.34, LengthUnit.CENTIMETER), 2.0);
        try
        {
            Field unitField = ClassUtil.resolveField(ipBreak2.getUnitParameter(), "value");
            unitField.setAccessible(true);
            unitField.set(ipBreak2.getUnitParameter(), SpeedUnit.KM_PER_HOUR);
        }
        catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e)
        {
            fail(e.getMessage());
        }
        Try.testFail(() -> { ipBreak2.setCalculatedValue(); });

        // break the map
        InputParameterDoubleScalar<LengthUnit, Length> ipBreak3 = new InputParameterDoubleScalar<>("len", "length",
                "length value", new Length(12.34, LengthUnit.CENTIMETER), 2.0);
        SortedMap<String, InputParameter<?, ?>> ipMap = ipBreak3.getValue();
        ipMap.remove("value");
        ipMap.remove("unit");
        Try.testFail(() -> { ipBreak3.getDoubleParameter(); });
        Try.testFail(() -> { ipBreak3.getUnitParameter(); });
    }

    /**
     * test FloatScalar input parameter.
     * @throws InputParameterException on setValue that did not work
     */
    @Test
    @SuppressWarnings("checkstyle:methodlength")
    public void testInputParameterFloatScalar() throws InputParameterException
    {
        InputParameterFloatScalar<LengthUnit, FloatLength> ip = new InputParameterFloatScalar<>("len", "length", "length value",
                new FloatLength(12.34f, LengthUnit.CENTIMETER), 2.0);
        InputParameterMap map = makeInputParameterMap();
        map.add(ip);
        assertEquals("len", ip.getKey());
        assertEquals("map.len", ip.getExtendedKey());
        assertEquals("length", ip.getShortName());
        assertEquals("length value", ip.getDescription());
        assertEquals(2.0, ip.getDisplayPriority(), 1E-6);
        assertEquals(12.34f, ip.getFloatParameter().getDefaultValue(), 1E-6);
        assertEquals(LengthUnit.CENTIMETER, ip.getUnitParameter().getDefaultValue());
        ip.setCalculatedValue();
        assertEquals(new FloatLength(12.34, LengthUnit.CENTIMETER), ip.getCalculatedValue());
        assertEquals(new FloatLength(12.34, LengthUnit.CENTIMETER), ip.getDefaultTypedValue());
        assertEquals(12.34f, ip.getFloatParameter().getValue(), 1E-6);
        assertEquals(LengthUnit.CENTIMETER, ip.getUnitParameter().getValue());
        assertEquals(-Float.MAX_VALUE, ip.getMinimumValueSI(), 1E-6);
        assertEquals(Float.MAX_VALUE, ip.getMaximumValueSI(), 1E-6);
        ip.getFloatParameter().setFloatValue(23.45f);
        ip.getUnitParameter().setMapValue(LengthUnit.KILOMETER);
        ip.setCalculatedValue();
        assertEquals(12.34f, ip.getFloatParameter().getDefaultValue(), 1E-6);
        assertEquals(LengthUnit.CENTIMETER, ip.getUnitParameter().getDefaultValue());
        assertEquals(new FloatLength(12.34, LengthUnit.CENTIMETER), ip.getDefaultTypedValue());
        assertEquals(new FloatLength(23.45, LengthUnit.KILOMETER), ip.getCalculatedValue());
        assertEquals(23.45f, ip.getFloatParameter().getValue(), 1E-6);
        assertEquals(LengthUnit.KILOMETER, ip.getUnitParameter().getValue());

        for (boolean includeMin : new boolean[] {false, true})
        {
            for (boolean includeMax : new boolean[] {false, true})
            {
                InputParameterFloatScalar<LengthUnit,
                        FloatLength> ipmm = new InputParameterFloatScalar<>("len", "length", "length value",
                                new FloatLength(12.34f, LengthUnit.CENTIMETER), new FloatLength(0.0f, LengthUnit.CENTIMETER),
                                new FloatLength(20.0f, LengthUnit.CENTIMETER), includeMin, includeMax, "%6.2f", 3.0);
                assertEquals(0.0, ipmm.getMinimumValueSI(), 1E-6);
                assertEquals(new FloatLength(20.0, LengthUnit.CENTIMETER).si, ipmm.getMaximumValueSI(), 1E-6);
                assertEquals("%6.2f", ipmm.getFloatParameter().getFormat());
                assertEquals(includeMin, ipmm.isMinIncluded());
                assertEquals(includeMax, ipmm.isMaxIncluded());
                ipmm.getFloatParameter().setValue(1.0f);
                ipmm.getUnitParameter().setValue(LengthUnit.NANOMETER);
                ipmm.setCalculatedValue();
                ipmm.getFloatParameter().setValue(199.0f);
                ipmm.getUnitParameter().setValue(LengthUnit.MILLIMETER);
                ipmm.setCalculatedValue();
                if (includeMin)
                {
                    ipmm.getFloatParameter().setFloatValue(0.0f);
                    ipmm.getUnitParameter().setValue(LengthUnit.CENTIMETER);
                    assertEquals(0.0f, ipmm.getFloatParameter().getValue(), 1E-6);
                    ipmm.setCalculatedValue();
                    assertEquals(FloatLength.ZERO, ipmm.getCalculatedValue());
                }
                else
                {
                    Try.testFail(() -> {
                        ipmm.getFloatParameter().setFloatValue(0.0f);
                        ipmm.getUnitParameter().setValue(LengthUnit.CENTIMETER);
                        ipmm.setCalculatedValue();
                    });
                }
                if (includeMax)
                {
                    ipmm.getFloatParameter().setFloatValue(20.0f);
                    ipmm.getUnitParameter().setValue(LengthUnit.CENTIMETER);
                    assertEquals(20.0f, ipmm.getFloatParameter().getValue(), 1E-6);
                    ipmm.setCalculatedValue();
                    assertEquals(new FloatLength(20.0, LengthUnit.CENTIMETER), ipmm.getCalculatedValue());
                }
                else
                {
                    Try.testFail(() -> {
                        ipmm.getFloatParameter().setFloatValue(20.0f);
                        ipmm.getUnitParameter().setValue(LengthUnit.CENTIMETER);
                        ipmm.setCalculatedValue();
                    });
                }
                Try.testFail(() -> {
                    ipmm.getFloatParameter().setFloatValue(Float.NaN);
                    ipmm.getUnitParameter().setValue(LengthUnit.METER);
                    ipmm.setCalculatedValue();
                });
                Try.testFail(() -> {
                    ipmm.getFloatParameter().setFloatValue(Float.POSITIVE_INFINITY);
                    ipmm.getUnitParameter().setValue(LengthUnit.METER);
                    ipmm.setCalculatedValue();
                });
                Try.testFail(() -> {
                    ipmm.getFloatParameter().setFloatValue(Float.NEGATIVE_INFINITY);
                    ipmm.getUnitParameter().setValue(LengthUnit.METER);
                    ipmm.setCalculatedValue();
                });
                Try.testFail(() -> {
                    ipmm.getFloatParameter().setFloatValue(-50.0f);
                    ipmm.getUnitParameter().setValue(LengthUnit.CENTIMETER);
                    ipmm.setCalculatedValue();
                });
                Try.testFail(() -> {
                    ipmm.getFloatParameter().setFloatValue(50.0f);
                    ipmm.getUnitParameter().setValue(LengthUnit.CENTIMETER);
                    ipmm.setCalculatedValue();
                });

                ipmm.getFloatParameter().setFormat("%8.4f");
                assertEquals("%8.4f", ipmm.getFloatParameter().getFormat());
                ipmm.setMinIncluded(!includeMin);
                assertEquals(!includeMin, ipmm.isMinIncluded());
                ipmm.setMaxIncluded(!includeMax);
                assertEquals(!includeMax, ipmm.isMaxIncluded());

                // this is how to set boundaries
                ipmm.setMinimumValueSI(-100.0f);
                assertEquals(-100.0, ipmm.getMinimumValueSI(), 1E-6);
                ipmm.setMaximumValueSI(100.0f);
                assertEquals(100.0, ipmm.getMaximumValueSI(), 1E-6);
                ipmm.getFloatParameter().setValue(-50.0f);
                ipmm.getUnitParameter().setValue(LengthUnit.METER);
                ipmm.setCalculatedValue();
                ipmm.getFloatParameter().setValue(50.0f);
                ipmm.getUnitParameter().setValue(LengthUnit.METER);
                ipmm.setCalculatedValue();
                Try.testFail(() -> {
                    ipmm.getFloatParameter().setFloatValue(-150.0f);
                    ipmm.getUnitParameter().setValue(LengthUnit.METER);
                    ipmm.setCalculatedValue();
                });
                Try.testFail(() -> {
                    ipmm.getFloatParameter().setFloatValue(150.0f);
                    ipmm.getUnitParameter().setValue(LengthUnit.METER);
                    ipmm.setCalculatedValue();
                });

                // not the way it is supposed to work, but you can limit the double parameter separately
                ipmm.getFloatParameter().setMinimumValue(-100.0f);
                assertEquals(-100.0f, ipmm.getFloatParameter().getMinimumValue(), 1E-6);
                ipmm.getFloatParameter().setMaximumValue(100.0f);
                assertEquals(100.0f, ipmm.getFloatParameter().getMaximumValue(), 1E-6);
                ipmm.getFloatParameter().setValue(-50.0f);
                ipmm.getFloatParameter().setValue(50.0f);
                Try.testFail(() -> { ipmm.getFloatParameter().setFloatValue(-150.0f); });
                Try.testFail(() -> { ipmm.getFloatParameter().setFloatValue(150.0f); });
            }
        }

        Try.testFail(() -> { ip.getFloatParameter().setFormat(null); });
        Try.testFail(() -> {
            new InputParameterFloatScalar<LengthUnit, FloatLength>("len", "len", "len",
                    new FloatLength(12.34, LengthUnit.CENTIMETER), new FloatLength(0.0, LengthUnit.CENTIMETER),
                    new FloatLength(20.0, LengthUnit.CENTIMETER), false, false, null, 3.0);
        });
        InputParameterFloatScalar<LengthUnit, FloatLength> ipClone = ip.clone();
        assertFalse(ip == ipClone);
        assertEquals(ip.getCalculatedValue(), ipClone.getCalculatedValue());

        InputParameterFloatScalar<LengthUnit, FloatLength> ipmmff = new InputParameterFloatScalar<>("d", "double",
                "double value", new FloatLength(12.34f, LengthUnit.CENTIMETER), 0.0f, 20.0f, false, false, "%6.2f", 3.0);

        Try.testFail(() -> {
            ipmmff.getFloatParameter().setFloatValue(0.0f);
            ipmmff.getUnitParameter().setMapValue(LengthUnit.HECTOMETER);
            ipmmff.setCalculatedValue();
        }, "Set value to 0.0 hm should have thrown exception");

        Try.testFail(() -> {
            ipmmff.getFloatParameter().setFloatValue(0.0f);
            ipmmff.getUnitParameter().setMapValue(LengthUnit.METER);
            ipmmff.setCalculatedValue();
        }, "Set value to 20.0 m should have thrown exception");

        Try.testFail(() -> {
            ipmmff.getFloatParameter().setFloatValue(Float.NaN);
            ipmmff.getUnitParameter().setMapValue(LengthUnit.METER);
            ipmmff.setCalculatedValue();
        }, "Set value to NaN m should have thrown exception");

        Try.testFail(() -> {
            ipmmff.getFloatParameter().setFloatValue(Float.POSITIVE_INFINITY);
            ipmmff.getUnitParameter().setMapValue(LengthUnit.METER);
            ipmmff.setCalculatedValue();
        }, "Set value to Inf m should have thrown exception");

        Try.testFail(() -> {
            ipmmff.getFloatParameter().setFloatValue(50.0f);
            ipmmff.getUnitParameter().setMapValue(LengthUnit.METER);
            ipmmff.setCalculatedValue();
        }, "Set value to 50 m should have thrown exception");

        Try.testFail(() -> {
            ipmmff.getFloatParameter().setFloatValue(1.0f);
            ipmmff.getUnitParameter().setMapValue(LengthUnit.KILOMETER);
            ipmmff.setCalculatedValue();
        }, "Set value to 1.0 km should have thrown exception");

        ipmmff.getFloatParameter().setFloatValue(1.0f);
        ipmmff.getUnitParameter().setMapValue(LengthUnit.METER);
        ipmmff.setCalculatedValue();

        ipmmff.getFloatParameter().setFloatValue(5000.0f);
        ipmmff.getUnitParameter().setMapValue(LengthUnit.MILLIMETER);
        ipmmff.setCalculatedValue();

        // break the NaN barrier
        InputParameterFloatScalar<LengthUnit, FloatLength> ipBreak1 = new InputParameterFloatScalar<>("len", "length",
                "length value", new FloatLength(12.34f, LengthUnit.CENTIMETER), 2.0);
        try
        {
            Field doubleField = ClassUtil.resolveField(ipBreak1.getFloatParameter(), "value");
            doubleField.setAccessible(true);
            doubleField.set(ipBreak1.getFloatParameter(), Float.NaN);
        }
        catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e)
        {
            fail(e.getMessage());
        }
        Try.testFail(() -> { ipBreak1.setCalculatedValue(); });

        // break the scalar constructor
        InputParameterFloatScalar<LengthUnit, FloatLength> ipBreak2 = new InputParameterFloatScalar<>("len", "length",
                "length value", new FloatLength(12.34f, LengthUnit.CENTIMETER), 2.0);
        try
        {
            Field unitField = ClassUtil.resolveField(ipBreak2.getUnitParameter(), "value");
            unitField.setAccessible(true);
            unitField.set(ipBreak2.getUnitParameter(), SpeedUnit.KM_PER_HOUR);
        }
        catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e)
        {
            fail(e.getMessage());
        }
        Try.testFail(() -> { ipBreak2.setCalculatedValue(); });

        // break the map
        InputParameterFloatScalar<LengthUnit, FloatLength> ipBreak3 = new InputParameterFloatScalar<>("len", "length",
                "length value", new FloatLength(12.34f, LengthUnit.CENTIMETER), 2.0);
        SortedMap<String, InputParameter<?, ?>> ipMap = ipBreak3.getValue();
        ipMap.remove("value");
        ipMap.remove("unit");
        Try.testFail(() -> { ipBreak3.getFloatParameter(); });
        Try.testFail(() -> { ipBreak3.getUnitParameter(); });
    }

    /**
     * test Unit input parameter.
     * @throws InputParameterException on setValue that did not work
     */
    @Test
    public void testInputParameterUnit() throws InputParameterException
    {
        InputParameterUnit<DurationUnit> ip =
                new InputParameterUnit<>("unit", "duration", "duration unit", DurationUnit.HOUR, 2.0);
        InputParameterMap map = makeInputParameterMap();
        map.add(ip);
        assertEquals("unit", ip.getKey());
        assertEquals("map.unit", ip.getExtendedKey());
        assertEquals("duration", ip.getShortName());
        assertEquals("duration unit", ip.getDescription());
        assertEquals(2.0, ip.getDisplayPriority(), 1E-6);
        assertEquals(DurationUnit.HOUR, ip.getDefaultValue());
        assertEquals(DurationUnit.HOUR, ip.getCalculatedValue());
        assertEquals(DurationUnit.HOUR, ip.getValue());
        ip.setValue(DurationUnit.DAY);
        assertEquals(DurationUnit.HOUR, ip.getDefaultValue());
        assertEquals(DurationUnit.DAY, ip.getCalculatedValue());
        assertEquals(DurationUnit.DAY, ip.getValue());
        ip.setObjectValue(DurationUnit.MINUTE);
        assertEquals(DurationUnit.HOUR, ip.getDefaultValue());
        assertEquals(DurationUnit.MINUTE, ip.getCalculatedValue());
        assertEquals(DurationUnit.MINUTE, ip.getValue());
        Try.testFail(() -> { ip.setValue(null); });
        Try.testFail(() -> { ip.setObjectValue(null); });
        Try.testFail(() -> { ip.setDefaultValue(null); });
        InputParameterUnit<DurationUnit> ipClone = ip.clone();
        assertFalse(ip == ipClone);
        assertEquals(ip.getCalculatedValue(), ipClone.getCalculatedValue());
        InputParameterMap map2 = map.clone();
        assertFalse(map == map2);

        SortedMap<String, DurationUnit> optionMap = ip.getOptions();
        optionMap.remove("second");
        Try.testFail(() -> { ip.setMapValue(DurationUnit.SECOND); });
        Try.testFail(() -> { ip.setKeyforValue("second"); });
        Try.testFail(() -> { ip.setKeyforValue("xyz"); });
        Try.testFail(() -> { ip.setKeyforValue(null); });
        ip.setKeyforValue("day");
        assertEquals("day", ip.getKeyforValue());

        SortedMap<String, String> stringMap = new TreeMap<>();
        stringMap.put("a", "a");
        Try.testFail(() -> { new InputParameterSelectionMap<String, String>("s", "s", "s", stringMap, "zz", 1.0); });
    }

}
