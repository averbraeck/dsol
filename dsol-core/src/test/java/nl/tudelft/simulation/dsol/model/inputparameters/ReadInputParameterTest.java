package nl.tudelft.simulation.dsol.model.inputparameters;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.unit.SpeedUnit;
import org.djunits.unit.TimeUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.scalar.Time;
import org.djunits.value.vfloat.scalar.FloatDuration;
import org.djutils.exceptions.Try;
import org.junit.Test;

import nl.tudelft.simulation.dsol.model.inputparameters.reader.ReadInputParameters;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteConstant;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteUniform;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.distributions.DistPoisson;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * ReadInputParameterTest.java. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class ReadInputParameterTest
{

    /**
     * test reading input parameters from string array.
     * @throws InputParameterException on uncaught error
     */
    @Test
    public void testReadInputParametersArgs() throws InputParameterException
    {
        InputParameterBoolean ip1 = new InputParameterBoolean("bool", "boolean", "boolean value", true, 1.0);
        InputParameterDouble ip2 = new InputParameterDouble("double", "double", "double value", 0.0, 2.0);
        InputParameterDoubleScalar<DurationUnit, Duration> ip3 =
                new InputParameterDoubleScalar<>("duration", "duration", "duration value", Duration.instantiateSI(0.0), 3.0);
        InputParameterString ip4 = new InputParameterString("string", "string", "string value", "abc", 4.0);
        InputParameterMap map = new InputParameterMap("map", "map", "map", 1.0);
        map.add(ip1);
        map.add(ip2);
        map.add(ip3);
        map.add(ip4);
        assertEquals(true, ip1.getDefaultValue());
        assertEquals(0.0, ip2.getDefaultValue(), 0.001);
        assertEquals(new Duration(0.0, DurationUnit.SI), ip3.getDefaultTypedValue());
        assertEquals("abc", ip4.getDefaultValue());

        String[] args = new String[] {"bool=false", "double=10.0", "duration=20min", "string=def"};
        ReadInputParameters.loadFromArgs(args, map);
        assertEquals(false, ip1.getCalculatedValue());
        assertEquals(10.0, ip2.getCalculatedValue(), 0.001);
        assertEquals(new Duration(20.0, DurationUnit.MINUTE), ip3.getCalculatedValue());
        assertEquals("def", ip4.getCalculatedValue());

        args = new String[] {"bool=false", "double=10.0", "duration='22.5 day'", "string='x y z'"};
        ReadInputParameters.loadFromArgs(args, map);
        assertEquals(new Duration(22.5, DurationUnit.DAY), ip3.getCalculatedValue());
        assertEquals("x y z", ip4.getCalculatedValue());

        args = new String[] {"string=\\'"};
        ReadInputParameters.loadFromArgs(args, map);
        assertEquals("'", ip4.getCalculatedValue());

        InputParameterInteger ip5 = new InputParameterInteger("int", "int", "int value between 0 and 10", 0, 0, 10, "%d", 5.0);
        map.add(ip5);
        assertEquals(0, ip5.getDefaultValue().intValue());
        args = new String[] {"int=5"};
        ReadInputParameters.loadFromArgs(args, map);
        assertEquals(5, ip5.getCalculatedValue().intValue());

        final String[] args1 = new String[] {"int=-10"};
        Try.testFail(() -> { ReadInputParameters.loadFromArgs(args1, map); }, "int=-10 should have given an error",
                InputParameterException.class);
        assertEquals(5, ip5.getCalculatedValue().intValue()); // value should be unchanged

        InputParameterLong ip6 =
                new InputParameterLong("long", "long", "long value between -10 and 10", 0L, -10L, 10L, "%d", 5.0);
        map.add(ip6);
        assertEquals(0L, ip6.getDefaultValue().intValue());
        args = new String[] {"long=-5"};
        ReadInputParameters.loadFromArgs(args, map);
        assertEquals(-5L, ip6.getCalculatedValue().intValue());

        final String[] args2 = new String[] {"int=200"};
        Try.testFail(() -> { ReadInputParameters.loadFromArgs(args2, map); }, "int=200 should have given an error",
                InputParameterException.class);
        assertEquals(-5L, ip6.getCalculatedValue().intValue()); // value should be unchanged
    }

    /**
     * test reading nested input parameters from string array.
     * @throws InputParameterException on uncaught error
     */
    @Test
    public void testReadNestedInputParametersArgs() throws InputParameterException
    {
        InputParameterBoolean ip1 = new InputParameterBoolean("bool", "boolean", "boolean value", true, 1.0);
        InputParameterFloat ip2 = new InputParameterFloat("float", "float", "float value", 0.0f, 2.0);
        InputParameterFloatScalar<DurationUnit, FloatDuration> ip3 = new InputParameterFloatScalar<>("duration", "duration",
                "duration value", FloatDuration.instantiateSI(0.0f), 3.0);
        InputParameterString ip4 = new InputParameterString("string", "string", "string value", "abc", 4.0);
        InputParameterMap map = new InputParameterMap("map", "map", "map", 1.0);
        InputParameterMap nest = new InputParameterMap("nest", "nest", "nested map", 1.0);
        nest.add(ip1);
        nest.add(ip2);
        nest.add(ip3);
        nest.add(ip4);
        map.add(nest);

        String[] args = new String[] {"nest.bool=false", "nest.float=10.0", "nest.duration=20min", "nest.string=def"};
        ReadInputParameters.loadFromArgs(args, map);
        assertEquals(false, ip1.getCalculatedValue());
        assertEquals(10.0f, ip2.getCalculatedValue(), 0.001);
        assertEquals(new FloatDuration(20.0, DurationUnit.MINUTE), ip3.getCalculatedValue());
        assertEquals("def", ip4.getCalculatedValue());

        args = new String[] {"nest.bool=false", "nest.float=10.0", "nest.duration='22.5 day'", "nest.string='x y z'"};
        ReadInputParameters.loadFromArgs(args, map);
        assertEquals(new FloatDuration(22.5, DurationUnit.DAY), ip3.getCalculatedValue());
        assertEquals("x y z", ip4.getCalculatedValue());
    }

    /**
     * test reading List input parameters from string array.
     * @throws InputParameterException on uncaught error
     */
    @Test
    public void testReadInputParametersArgsList() throws InputParameterException
    {
        String[] options = new String[] {"US", "GB", "NL", "DE", "BE", "FR"};
        InputParameterSelectionList<String> ip =
                new InputParameterSelectionList<>("list", "list", "list value", options, "US", 1.0);
        InputParameterMap map = new InputParameterMap("map", "map", "map", 1.0);
        map.add(ip);
        assertEquals("US", ip.getDefaultValue());

        String[] args = new String[] {"list=NL"};
        ReadInputParameters.loadFromArgs(args, map);
        assertEquals("NL", ip.getCalculatedValue());

        final String[] args1 = new String[] {"list=XY"};
        Try.testFail(() -> { ReadInputParameters.loadFromArgs(args1, map); }, "list=XY should have given an error",
                InputParameterException.class);
        assertEquals("NL", ip.getCalculatedValue()); // value should be unchanged

    }

    /**
     * test reading List input parameters from string array.
     * @throws InputParameterException on uncaught error
     */
    @Test
    public void testReadInputParametersArgsDist() throws InputParameterException
    {
        StreamInterface stream = new MersenneTwister(1234L);
        DistDiscrete dddefault = new DistDiscreteConstant(stream, 0L);
        DistContinuous dcdefault = new DistConstant(stream, 0.0);
        InputParameterDistDiscrete ipd =
                new InputParameterDistDiscrete("dd", "distdiscrete", "discrete distribution value", stream, dddefault, 1.0);
        InputParameterDistContinuous ipc = new InputParameterDistContinuous("dc", "distcontinuous",
                "continuous distribution value", stream, dcdefault, 2.0);
        InputParameterMap map = new InputParameterMap("map", "map", "map", 1.0);
        map.add(ipd);
        map.add(ipc);

        String[] args = new String[] {"dd=Poisson(2)", "dc=Exponential(4.2)"};
        ReadInputParameters.loadFromArgs(args, map);
        assertEquals("DistPoisson", ipd.getValue().getClass().getSimpleName());
        DistPoisson dp = (DistPoisson) ipd.getValue();
        assertEquals(2.0, dp.getLambda(), 0.0001);
        assertEquals("DistExponential", ipc.getValue().getClass().getSimpleName());
        DistExponential de = (DistExponential) ipc.getValue();
        assertEquals(4.2, de.getMean(), 0.0001);

        args = new String[] {"dd=DiscreteUniform(10,20)", "dc=TRIA(10,20,30)"};
        ReadInputParameters.loadFromArgs(args, map);
        assertEquals("DistDiscreteUniform", ipd.getValue().getClass().getSimpleName());
        DistDiscreteUniform ddu = (DistDiscreteUniform) ipd.getValue();
        assertEquals(10L, ddu.getMin());
        assertEquals(20L, ddu.getMax());
        assertEquals("DistTriangular", ipc.getValue().getClass().getSimpleName());
        DistTriangular dt = (DistTriangular) ipc.getValue();
        assertEquals(10.0, dt.getMin(), 0.0001);
        assertEquals(20.0, dt.getMode(), 0.0001);
        assertEquals(30.0, dt.getMax(), 0.0001);

        // with spaces
        args = new String[] {"dd=DiscreteUniform(10, 20)", "dc=TRIA(10, 20, 30)"};
        ReadInputParameters.loadFromArgs(args, map);
        assertEquals("DistDiscreteUniform", ipd.getValue().getClass().getSimpleName());
        ddu = (DistDiscreteUniform) ipd.getValue();
        assertEquals(10L, ddu.getMin());
        assertEquals(20L, ddu.getMax());
        assertEquals("DistTriangular", ipc.getValue().getClass().getSimpleName());
        dt = (DistTriangular) ipc.getValue();
        assertEquals(10.0, dt.getMin(), 0.0001);
        assertEquals(20.0, dt.getMode(), 0.0001);
        assertEquals(30.0, dt.getMax(), 0.0001);

        // wrong nr of args
        final String[] args1 = new String[] {"dc=TRIA(10,20)"};
        Try.testFail(() -> { ReadInputParameters.loadFromArgs(args1, map); }, "dc=TRIA(10,20) should have given an error");
    }

    /**
     * test reading input parameters from string array.
     * @throws InputParameterException on uncaught error
     * @throws IOException on i/o error
     * @throws FileNotFoundException on file error
     */
    @Test
    public void testReadInputParametersFile() throws InputParameterException, FileNotFoundException, IOException
    {
        InputParameterMap map = new InputParameterMap("map", "map", "map", 1.0);
        InputParameterDoubleScalar<TimeUnit, Time> ipStartTime =
                new InputParameterDoubleScalar<>("startTime", "startTime", "startTime value", Time.ZERO, 1.0);
        InputParameterDoubleScalar<DurationUnit, Duration> ipRunTime =
                new InputParameterDoubleScalar<>("runTime", "runTime", "runTime value", Duration.ZERO, 2.0);
        map.add(ipStartTime);
        map.add(ipRunTime);

        InputParameterMap server1 = new InputParameterMap("server1", "server1", "server1", 3.0);
        InputParameterInteger ipN =
                new InputParameterInteger("n", "n", "n value (positive)", 0, 0, Integer.MAX_VALUE, "%d", 1.0);
        InputParameterDoubleScalar<DurationUnit, Duration> ipProcTime =
                new InputParameterDoubleScalar<>("procTime", "procTime", "procTime value", Duration.ZERO, 2.0);
        server1.add(ipN);
        server1.add(ipProcTime);
        map.add(server1);

        InputParameterMap conveyor = new InputParameterMap("conveyor", "conveyor", "conveyor", 4.0);
        InputParameterDoubleScalar<LengthUnit, Length> ipLength =
                new InputParameterDoubleScalar<>("length", "length", "length value", Length.ZERO, 1.0);
        InputParameterDoubleScalar<SpeedUnit, Speed> ipSpeed =
                new InputParameterDoubleScalar<>("speed", "speed", "speed value", Speed.ZERO, 1.0);
        conveyor.add(ipLength);
        conveyor.add(ipSpeed);
        map.add(conveyor);

        ReadInputParameters.loadfromProperties("/paramtest1.properties", map);

        /*-
        startTime = 1 day
        runTime = 24 hrs
        server1.n = 1
        server1.procTime = 5 min
        conveyor.length = 10 m
        conveyor.speed = 2.4 m/s
        */

        assertEquals(new Time(1.0, TimeUnit.BASE_DAY), ipStartTime.getCalculatedValue());
        assertEquals(new Duration(24.0, DurationUnit.HOUR), ipRunTime.getCalculatedValue());
        assertEquals(1, ipN.getCalculatedValue().intValue());
        assertEquals(new Duration(5.0, DurationUnit.MINUTE), ipProcTime.getCalculatedValue());
        assertEquals(new Length(10.0, LengthUnit.METER), ipLength.getCalculatedValue());
        assertEquals(new Speed(2.4, SpeedUnit.METER_PER_SECOND), ipSpeed.getCalculatedValue());
    }
}
