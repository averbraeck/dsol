package nl.tudelft.simulation.dsol.model.inputparameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.djutils.exceptions.Try;
import org.junit.Test;

import nl.tudelft.simulation.jstats.distributions.DistBernoulli;
import nl.tudelft.simulation.jstats.distributions.DistBeta;
import nl.tudelft.simulation.jstats.distributions.DistBinomial;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteConstant;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteUniform;
import nl.tudelft.simulation.jstats.distributions.DistErlang;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.distributions.DistGamma;
import nl.tudelft.simulation.jstats.distributions.DistGeometric;
import nl.tudelft.simulation.jstats.distributions.DistLogNormal;
import nl.tudelft.simulation.jstats.distributions.DistNegBinomial;
import nl.tudelft.simulation.jstats.distributions.DistNormal;
import nl.tudelft.simulation.jstats.distributions.DistPearson5;
import nl.tudelft.simulation.jstats.distributions.DistPearson6;
import nl.tudelft.simulation.jstats.distributions.DistPoisson;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.jstats.distributions.DistUniform;
import nl.tudelft.simulation.jstats.distributions.DistWeibull;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * InputParameterTest for Distributions. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InputParameterDistTest
{
    /**
     * Test continuous distributions input parameter.
     * @throws InputParameterException on setting error
     */
    @Test
    public void testInputParameterDistContinuous() throws InputParameterException
    {
        StreamInterface stream = new MersenneTwister(20L);
        InputParameterDistContinuous ip = new InputParameterDistContinuous("dist", "distribution", "continuous distribution",
                stream, new DistExponential(stream, 2.0), 1.0);
        assertEquals("dist", ip.getKey());
        assertEquals("dist", ip.getExtendedKey());
        assertEquals("distribution", ip.getShortName());
        assertEquals("continuous distribution", ip.getDescription());
        assertEquals(1.0, ip.getDisplayPriority(), 1E-6);
        assertEquals(2.0, ((DistExponential) ip.getDefaultValue()).getMean(), 1E-6);
        assertEquals(stream, ((DistExponential) ip.getDefaultValue()).getStream());
        assertEquals(2.0, ((DistExponential) ip.getCalculatedValue()).getMean(), 1E-6);
        assertEquals(stream, ((DistExponential) ip.getCalculatedValue()).getStream());
        assertEquals(2.0, ((DistExponential) ip.getValue()).getMean(), 1E-6);
        assertEquals(stream, ((DistExponential) ip.getValue()).getStream());

        ip.setDistValue(new DistUniform(stream, 1.0, 2.0));
        assertTrue(ip.getDefaultValue() instanceof DistExponential);
        assertTrue(ip.getCalculatedValue() instanceof DistUniform);
        assertTrue(ip.getValue() instanceof DistUniform);

        InputParameterMap map = new InputParameterMap("map", "parametermap", "parameter map", 10.0);
        map.add(ip);
        assertEquals("dist", ip.getKey());
        assertEquals("map.dist", ip.getExtendedKey());

        ip = new InputParameterDistContinuous("dist", "distribution", "continuous distribution", stream,
                new DistExponential(stream, 2.0), 1.0);
        double v = ip.getValue().draw();
        double d = ip.getDefaultValue().draw();
        ip.setStream(new MersenneTwister(20L));
        assertEquals(v, ip.getValue().draw(), 1E-6);
        assertEquals(d, ip.getDefaultValue().draw(), 1E-6);

        InputParameterDistContinuous ipClone = ip.clone();
        assertFalse(ip == ipClone);
        assertEquals(((DistExponential) ip.getDefaultValue()).getMean(),
                ((DistExponential) ipClone.getDefaultValue()).getMean(), 1E-6);
        assertEquals(((DistExponential) ip.getValue()).getMean(), ((DistExponential) ipClone.getValue()).getMean(), 1E-6);

        Try.testFail(() -> { ipClone.setStream(null); });
        Try.testFail(() -> { ipClone.setDistValue(null); });
        Try.testFail(() -> {
            new InputParameterDistContinuous("dist", "distribution", "continuous distribution", null,
                    new DistExponential(stream, 2.0), 1.0);
        });
        Try.testFail(() -> {
            new InputParameterDistContinuous("dist", "distribution", "continuous distribution", stream, null, 1.0);
        });
    }

    /**
     * Test discrete distributions input parameter.
     * @throws InputParameterException on setting error
     */
    @Test
    public void testInputParameterDistDiscrete() throws InputParameterException
    {
        StreamInterface stream = new MersenneTwister(20L);
        InputParameterDistDiscrete ip = new InputParameterDistDiscrete("dist", "distribution", "discrete distribution", stream,
                new DistPoisson(stream, 2.0), 1.0);
        assertEquals("dist", ip.getKey());
        assertEquals("dist", ip.getExtendedKey());
        assertEquals("distribution", ip.getShortName());
        assertEquals("discrete distribution", ip.getDescription());
        assertEquals(1.0, ip.getDisplayPriority(), 1E-6);
        assertEquals(2.0, ((DistPoisson) ip.getDefaultValue()).getLambda(), 1E-6);
        assertEquals(stream, ((DistPoisson) ip.getDefaultValue()).getStream());
        assertEquals(2.0, ((DistPoisson) ip.getCalculatedValue()).getLambda(), 1E-6);
        assertEquals(stream, ((DistPoisson) ip.getCalculatedValue()).getStream());
        assertEquals(2.0, ((DistPoisson) ip.getValue()).getLambda(), 1E-6);
        assertEquals(stream, ((DistPoisson) ip.getValue()).getStream());

        ip.setDistValue(new DistBernoulli(stream, 0.5));
        assertTrue(ip.getDefaultValue() instanceof DistPoisson);
        assertTrue(ip.getCalculatedValue() instanceof DistBernoulli);
        assertTrue(ip.getValue() instanceof DistBernoulli);

        InputParameterMap map = new InputParameterMap("map", "parametermap", "parameter map", 10.0);
        map.add(ip);
        assertEquals("dist", ip.getKey());
        assertEquals("map.dist", ip.getExtendedKey());

        ip = new InputParameterDistDiscrete("dist", "distribution", "discrete distribution", stream,
                new DistPoisson(stream, 2.0), 1.0);
        long v = ip.getValue().draw();
        long d = ip.getDefaultValue().draw();
        ip.setStream(new MersenneTwister(20L));
        assertEquals(v, ip.getValue().draw(), 1E-6);
        assertEquals(d, ip.getDefaultValue().draw());

        InputParameterDistDiscrete ipClone = ip.clone();
        assertFalse(ip == ipClone);
        assertEquals(((DistPoisson) ip.getDefaultValue()).getLambda(), ((DistPoisson) ipClone.getDefaultValue()).getLambda(),
                1E-6);
        assertEquals(((DistPoisson) ip.getValue()).getLambda(), ((DistPoisson) ipClone.getValue()).getLambda(), 1E-6);

        Try.testFail(() -> { ipClone.setStream(null); });
        Try.testFail(() -> { ipClone.setDistValue(null); });
        Try.testFail(() -> {
            new InputParameterDistDiscrete("dist", "distribution", "discrete distribution", null, new DistPoisson(stream, 2.0),
                    1.0);
        });
        Try.testFail(
                () -> { new InputParameterDistDiscrete("dist", "distribution", "discrete distribution", stream, null, 1.0); });
    }

    /**
     * Test continuous distributions selection input parameter.
     * @throws InputParameterException on setting error
     */
    @Test
    @SuppressWarnings("checkstyle:methodlength")
    public void testInputParameterDistContinuousSelection() throws InputParameterException
    {
        StreamInterface stream = new MersenneTwister(20L);
        InputParameterDistContinuousSelection ip =
                new InputParameterDistContinuousSelection("dist", "distribution", "continuous distribution", stream, 1.0);
        assertEquals("dist", ip.getKey());
        assertEquals("dist", ip.getExtendedKey());
        assertEquals("distribution", ip.getShortName());
        assertEquals("continuous distribution", ip.getDescription());
        assertEquals(stream, ip.getStream());
        assertEquals(1.0, ip.getDisplayPriority(), 1E-6);

        // Constant
        ip.setValue(InputParameterDistContinuousSelection.getDistOptions().get("Constant"));
        ((InputParameterDouble) ip.getValue().get("c")).setValue(1.5);
        ip.getValue().setDist();
        assertTrue(ip.getDist() instanceof DistConstant);
        assertEquals(1.5, ((DistConstant) ip.getDist()).getConstant(), 1E-6);
        assertTrue(ip.getValue().printValues().contains("c = "));

        // test backing map
        Try.testFail(() -> { ip.getValue().add(null); });
        Try.testFail(() -> { ip.getValue().get("abc"); });
        Try.testFail(() -> { ip.getValue().remove("abc"); });
        ip.getValue().add(new InputParameterDouble("d", "d", "d", 1.0, 2.0));
        ip.getValue().remove("d");
        InputParameterMap map = new InputParameterMap("map", "parametermap", "parameter map", 10.0);
        map.add(ip);
        ip.getValue().add(new InputParameterDouble("d", "d", "d", 1.0, 2.0));
        map.getValue().remove("map.Constant.d");
        assertTrue(map.printValues().length() > 0);

        // Beta
        ip.setValue(InputParameterDistContinuousSelection.getDistOptions().get("Beta"));
        ((InputParameterDouble) ip.getValue().get("alpha1")).setValue(1.5);
        ((InputParameterDouble) ip.getValue().get("alpha2")).setValue(2.5);
        ip.getValue().setDist();
        assertTrue(ip.getDist() instanceof DistBeta);
        assertEquals(1.5, ((DistBeta) ip.getDist()).getAlpha1(), 1E-6);
        assertEquals(2.5, ((DistBeta) ip.getDist()).getAlpha2(), 1E-6);
        assertTrue(ip.getValue().printValues().contains("alpha1 = "));
        ((InputParameterDouble) ip.getValue().get("alpha1")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("alpha1")).setValue(1.0);
        ((InputParameterDouble) ip.getValue().get("alpha2")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("alpha2")).setValue(1.0);

        // Erlang
        ip.setValue(InputParameterDistContinuousSelection.getDistOptions().get("Erlang"));
        ((InputParameterDouble) ip.getValue().get("scale")).setValue(1.5);
        ((InputParameterInteger) ip.getValue().get("k")).setValue(5);
        ip.getValue().setDist();
        assertTrue(ip.getDist() instanceof DistErlang);
        assertEquals(1.5, ((DistErlang) ip.getDist()).getScale(), 1E-6);
        assertEquals(5, ((DistErlang) ip.getDist()).getK());
        assertTrue(ip.getValue().printValues().contains("scale = "));
        ((InputParameterDouble) ip.getValue().get("scale")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("scale")).setValue(1.0);
        ((InputParameterInteger) ip.getValue().get("k")).setValue(-1);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterInteger) ip.getValue().get("k")).setValue(2);

        // Exponential
        ip.setValue(InputParameterDistContinuousSelection.getDistOptions().get("Exponential"));
        ((InputParameterDouble) ip.getValue().get("lambda")).setValue(1.5);
        ip.getValue().setDist();
        assertTrue(ip.getDist() instanceof DistExponential);
        assertEquals(1.5, ((DistExponential) ip.getDist()).getMean(), 1E-6);
        assertTrue(ip.getValue().printValues().contains("lambda = "));
        ((InputParameterDouble) ip.getValue().get("lambda")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("lambda")).setValue(1.0);

        // Gamma
        ip.setValue(InputParameterDistContinuousSelection.getDistOptions().get("Gamma"));
        ((InputParameterDouble) ip.getValue().get("shape")).setValue(1.5);
        ((InputParameterDouble) ip.getValue().get("scale")).setValue(2.5);
        ip.getValue().setDist();
        assertTrue(ip.getDist() instanceof DistGamma);
        assertEquals(1.5, ((DistGamma) ip.getDist()).getShape(), 1E-6);
        assertEquals(2.5, ((DistGamma) ip.getDist()).getScale(), 1E-6);
        assertTrue(ip.getValue().printValues().contains("shape = "));
        ((InputParameterDouble) ip.getValue().get("shape")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("shape")).setValue(1.0);
        ((InputParameterDouble) ip.getValue().get("scale")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("scale")).setValue(1.0);

        // LogNormal
        ip.setValue(InputParameterDistContinuousSelection.getDistOptions().get("LogNormal"));
        ((InputParameterDouble) ip.getValue().get("mu")).setValue(1.5);
        ((InputParameterDouble) ip.getValue().get("sigma")).setValue(0.5);
        ip.getValue().setDist();
        assertTrue(ip.getDist() instanceof DistLogNormal);
        assertEquals(1.5, ((DistLogNormal) ip.getDist()).getMu(), 1E-6);
        assertEquals(0.5, ((DistLogNormal) ip.getDist()).getSigma(), 1E-6);
        assertTrue(ip.getValue().printValues().contains("mu = "));
        ((InputParameterDouble) ip.getValue().get("sigma")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("sigma")).setValue(1.0);

        // Normal
        ip.setValue(InputParameterDistContinuousSelection.getDistOptions().get("Normal"));
        ((InputParameterDouble) ip.getValue().get("mu")).setValue(1.5);
        ((InputParameterDouble) ip.getValue().get("sigma")).setValue(0.5);
        ip.getValue().setDist();
        assertTrue(ip.getDist() instanceof DistNormal);
        assertEquals(1.5, ((DistNormal) ip.getDist()).getMu(), 1E-6);
        assertEquals(0.5, ((DistNormal) ip.getDist()).getSigma(), 1E-6);
        assertTrue(ip.getValue().printValues().contains("mu = "));
        ((InputParameterDouble) ip.getValue().get("sigma")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("sigma")).setValue(1.0);

        // Pearson5
        ip.setValue(InputParameterDistContinuousSelection.getDistOptions().get("Pearson5"));
        ((InputParameterDouble) ip.getValue().get("alpha")).setValue(1.5);
        ((InputParameterDouble) ip.getValue().get("beta")).setValue(2.5);
        ip.getValue().setDist();
        assertTrue(ip.getDist() instanceof DistPearson5);
        assertEquals(1.5, ((DistPearson5) ip.getDist()).getAlpha(), 1E-6);
        assertEquals(2.5, ((DistPearson5) ip.getDist()).getBeta(), 1E-6);
        assertTrue(ip.getValue().printValues().contains("alpha = "));
        ((InputParameterDouble) ip.getValue().get("alpha")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("alpha")).setValue(1.0);
        ((InputParameterDouble) ip.getValue().get("beta")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("beta")).setValue(1.0);

        // Pearson6
        ip.setValue(InputParameterDistContinuousSelection.getDistOptions().get("Pearson6"));
        ((InputParameterDouble) ip.getValue().get("alpha1")).setValue(1.5);
        ((InputParameterDouble) ip.getValue().get("alpha2")).setValue(2.5);
        ((InputParameterDouble) ip.getValue().get("beta")).setValue(3.5);
        ip.getValue().setDist();
        assertTrue(ip.getDist() instanceof DistPearson6);
        assertEquals(1.5, ((DistPearson6) ip.getDist()).getAlpha1(), 1E-6);
        assertEquals(2.5, ((DistPearson6) ip.getDist()).getAlpha2(), 1E-6);
        assertEquals(3.5, ((DistPearson6) ip.getDist()).getBeta(), 1E-6);
        assertTrue(ip.getValue().printValues().contains("alpha1 = "));
        ((InputParameterDouble) ip.getValue().get("alpha1")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("alpha1")).setValue(1.0);
        ((InputParameterDouble) ip.getValue().get("alpha2")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("alpha2")).setValue(1.0);
        ((InputParameterDouble) ip.getValue().get("beta")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("beta")).setValue(1.0);

        // Triangular
        ip.setValue(InputParameterDistContinuousSelection.getDistOptions().get("Triangular"));
        ((InputParameterDouble) ip.getValue().get("min")).setValue(1.5);
        ((InputParameterDouble) ip.getValue().get("mode")).setValue(2.5);
        ((InputParameterDouble) ip.getValue().get("max")).setValue(3.5);
        ip.getValue().setDist();
        assertTrue(ip.getDist() instanceof DistTriangular);
        assertEquals(1.5, ((DistTriangular) ip.getDist()).getMin(), 1E-6);
        assertEquals(2.5, ((DistTriangular) ip.getDist()).getMode(), 1E-6);
        assertEquals(3.5, ((DistTriangular) ip.getDist()).getMax(), 1E-6);
        assertTrue(ip.getValue().printValues().contains("min = "));
        ((InputParameterDouble) ip.getValue().get("min")).setValue(5.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("min")).setValue(1.5);
        ((InputParameterDouble) ip.getValue().get("mode")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("mode")).setValue(2.5);
        ((InputParameterDouble) ip.getValue().get("max")).setValue(2.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("max")).setValue(3.5);
        ((InputParameterDouble) ip.getValue().get("min")).setValue(1.5);
        ((InputParameterDouble) ip.getValue().get("mode")).setValue(1.5);
        ip.getValue().setDist();
        ((InputParameterDouble) ip.getValue().get("max")).setValue(1.5);
        Try.testFail(() -> { ip.getValue().setDist(); });

        // Uniform
        ip.setValue(InputParameterDistContinuousSelection.getDistOptions().get("Uniform"));
        ((InputParameterDouble) ip.getValue().get("min")).setValue(1.5);
        ((InputParameterDouble) ip.getValue().get("max")).setValue(3.5);
        ip.getValue().setDist();
        assertTrue(ip.getDist() instanceof DistUniform);
        assertEquals(1.5, ((DistUniform) ip.getDist()).getMin(), 1E-6);
        assertEquals(3.5, ((DistUniform) ip.getDist()).getMax(), 1E-6);
        assertTrue(ip.getValue().printValues().contains("min = "));
        ((InputParameterDouble) ip.getValue().get("min")).setValue(5.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("min")).setValue(1.5);
        ((InputParameterDouble) ip.getValue().get("min")).setValue(3.5);
        Try.testFail(() -> { ip.getValue().setDist(); });

        // Weibull
        ip.setValue(InputParameterDistContinuousSelection.getDistOptions().get("Weibull"));
        ((InputParameterDouble) ip.getValue().get("alpha")).setValue(1.5);
        ((InputParameterDouble) ip.getValue().get("beta")).setValue(2.5);
        ip.getValue().setDist();
        assertTrue(ip.getDist() instanceof DistWeibull);
        assertEquals(1.5, ((DistWeibull) ip.getDist()).getAlpha(), 1E-6);
        assertEquals(2.5, ((DistWeibull) ip.getDist()).getBeta(), 1E-6);
        assertTrue(ip.getValue().printValues().contains("alpha = "));
        ((InputParameterDouble) ip.getValue().get("alpha")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("alpha")).setValue(1.0);
        ((InputParameterDouble) ip.getValue().get("beta")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("beta")).setValue(1.0);
    }

    /**
     * Test discrete distributions selection input parameter.
     * @throws InputParameterException on setting error
     */
    @Test
    @SuppressWarnings("checkstyle:methodlength")
    public void testInputParameterDistDiscreteSelection() throws InputParameterException
    {
        StreamInterface stream = new MersenneTwister(20L);
        InputParameterDistDiscreteSelection ip =
                new InputParameterDistDiscreteSelection("dist", "distribution", "discrete distribution", stream, 1.0);
        assertEquals("dist", ip.getKey());
        assertEquals("dist", ip.getExtendedKey());
        assertEquals("distribution", ip.getShortName());
        assertEquals("discrete distribution", ip.getDescription());
        assertEquals(stream, ip.getStream());
        assertEquals(1.0, ip.getDisplayPriority(), 1E-6);

        // DiscreteConstant
        ip.setValue(InputParameterDistDiscreteSelection.getDistOptions().get("DiscreteConstant"));
        ((InputParameterLong) ip.getValue().get("c")).setValue(5L);
        ip.getValue().setDist();
        assertTrue(ip.getDist() instanceof DistDiscreteConstant);
        assertEquals(5L, ((DistDiscreteConstant) ip.getDist()).getConstant());
        assertTrue(ip.getValue().printValues().contains("c = "));

        // test backing map
        Try.testFail(() -> { ip.getValue().add(null); });
        Try.testFail(() -> { ip.getValue().get("abc"); });
        Try.testFail(() -> { ip.getValue().remove("abc"); });
        ip.getValue().add(new InputParameterDouble("d", "d", "d", 1.0, 2.0));
        ip.getValue().remove("d");
        InputParameterMap map = new InputParameterMap("map", "parametermap", "parameter map", 10.0);
        map.add(ip);
        ip.getValue().add(new InputParameterDouble("d", "d", "d", 1.0, 2.0));
        map.getValue().remove("map.DiscreteConstant.d");
        assertTrue(map.printValues().length() > 0);

        // Bernoulli
        ip.setValue(InputParameterDistDiscreteSelection.getDistOptions().get("Bernoulli"));
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.25);
        ip.getValue().setDist();
        assertTrue(ip.getDist() instanceof DistBernoulli);
        assertEquals(0.25, ((DistBernoulli) ip.getDist()).getP(), 1E-6);
        assertTrue(ip.getValue().printValues().contains("p = "));
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.5);
        ((InputParameterDouble) ip.getValue().get("p")).setValue(1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.5);
        ((InputParameterDouble) ip.getValue().get("p")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.5);
        ((InputParameterDouble) ip.getValue().get("p")).setValue(2.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.5);

        // Binomial
        ip.setValue(InputParameterDistDiscreteSelection.getDistOptions().get("Binomial"));
        ((InputParameterInteger) ip.getValue().get("n")).setValue(5);
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.25);
        ip.getValue().setDist();
        assertTrue(ip.getDist() instanceof DistBinomial);
        assertEquals(0.25, ((DistBinomial) ip.getDist()).getP(), 1E-6);
        assertEquals(5, ((DistBinomial) ip.getDist()).getN(), 1E-6);
        assertTrue(ip.getValue().printValues().contains("p = "));
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.5);
        ((InputParameterDouble) ip.getValue().get("p")).setValue(1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.5);
        ((InputParameterDouble) ip.getValue().get("p")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.5);
        ((InputParameterDouble) ip.getValue().get("p")).setValue(2.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.5);
        ((InputParameterInteger) ip.getValue().get("n")).setValue(0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterInteger) ip.getValue().get("n")).setValue(5);
        ((InputParameterInteger) ip.getValue().get("n")).setValue(-1);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterInteger) ip.getValue().get("n")).setValue(5);

        // DiscreteUniform
        ip.setValue(InputParameterDistDiscreteSelection.getDistOptions().get("DiscreteUniform"));
        ((InputParameterLong) ip.getValue().get("min")).setValue(5L);
        ((InputParameterLong) ip.getValue().get("max")).setValue(10L);
        ip.getValue().setDist();
        assertTrue(ip.getDist() instanceof DistDiscreteUniform);
        assertEquals(5L, ((DistDiscreteUniform) ip.getDist()).getMin(), 1E-6);
        assertEquals(10L, ((DistDiscreteUniform) ip.getDist()).getMax(), 1E-6);
        assertTrue(ip.getValue().printValues().contains("min = "));
        ((InputParameterLong) ip.getValue().get("min")).setValue(10L);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterLong) ip.getValue().get("min")).setValue(5L);
        ((InputParameterLong) ip.getValue().get("min")).setValue(15L);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterLong) ip.getValue().get("min")).setValue(5L);

        // Geometric
        ip.setValue(InputParameterDistDiscreteSelection.getDistOptions().get("Geometric"));
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.25);
        ip.getValue().setDist();
        assertTrue(ip.getDist() instanceof DistGeometric);
        assertEquals(0.25, ((DistGeometric) ip.getDist()).getP(), 1E-6);
        assertTrue(ip.getValue().printValues().contains("p = "));
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.5);
        ((InputParameterDouble) ip.getValue().get("p")).setValue(1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.5);
        ((InputParameterDouble) ip.getValue().get("p")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.5);
        ((InputParameterDouble) ip.getValue().get("p")).setValue(2.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.5);

        // NegBinomial
        ip.setValue(InputParameterDistDiscreteSelection.getDistOptions().get("NegBinomial"));
        ((InputParameterInteger) ip.getValue().get("n")).setValue(5);
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.25);
        ip.getValue().setDist();
        assertTrue(ip.getDist() instanceof DistNegBinomial);
        assertEquals(0.25, ((DistNegBinomial) ip.getDist()).getP(), 1E-6);
        assertEquals(5, ((DistNegBinomial) ip.getDist()).getS(), 1E-6);
        assertTrue(ip.getValue().printValues().contains("p = "));
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.5);
        ((InputParameterDouble) ip.getValue().get("p")).setValue(1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.5);
        ((InputParameterDouble) ip.getValue().get("p")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.5);
        ((InputParameterDouble) ip.getValue().get("p")).setValue(2.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("p")).setValue(0.5);
        ((InputParameterInteger) ip.getValue().get("n")).setValue(0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterInteger) ip.getValue().get("n")).setValue(5);
        ((InputParameterInteger) ip.getValue().get("n")).setValue(-1);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterInteger) ip.getValue().get("n")).setValue(5);

        // Poisson
        ip.setValue(InputParameterDistDiscreteSelection.getDistOptions().get("Poisson"));
        ((InputParameterDouble) ip.getValue().get("lambda")).setValue(2.5);
        ip.getValue().setDist();
        assertTrue(ip.getDist() instanceof DistPoisson);
        assertEquals(2.5, ((DistPoisson) ip.getDist()).getLambda(), 1E-6);
        assertTrue(ip.getValue().printValues().contains("lambda = "));
        ((InputParameterDouble) ip.getValue().get("lambda")).setValue(0.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("lambda")).setValue(2.5);
        ((InputParameterDouble) ip.getValue().get("lambda")).setValue(-1.0);
        Try.testFail(() -> { ip.getValue().setDist(); });
        ((InputParameterDouble) ip.getValue().get("lambda")).setValue(2.5);
    }
}
