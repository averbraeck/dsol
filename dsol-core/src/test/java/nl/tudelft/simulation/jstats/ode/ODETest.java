package nl.tudelft.simulation.jstats.ode;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;

import org.junit.Test;

import nl.tudelft.simulation.jstats.ode.integrators.NumericalIntegratorType;

/**
 * The test script for the ODE package.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @since 1.5
 */
public class ODETest
{
    /**
     * tests the differential equation solvers on y' = x.y^(1/3); see F. Scheid, Numerical Analysis, 2nd Edition, p.204 (Schaum
     * Outline Series). for x=1..5, solutions are: 1: 1.0; 2: 2.82843; 3: 7.02113; 4: 14.69694; 5: 27.0.
     */
    @Test
    public void testODE()
    {
        for (NumericalIntegratorType integrationMethod : NumericalIntegratorType.values())
        {
            ODE ode = new ODE(0.01, integrationMethod);
            String m = "Method " + integrationMethod.toString() + ", x = ";
            double[] result = ode.y(1);
            assertEquals(m + "1", 1.0, result[0], 0.1);
            result = ode.y(2);
            assertEquals(m + "2", 2.82843, result[0], 0.5);
            result = ode.y(3);
            assertEquals(m + "3", 7.02113, result[0], 0.5);
            result = ode.y(4);
            assertEquals(m + "4", 14.69694, result[0], 0.5);
            result = ode.y(5);
            assertEquals(m + "5", 27.0, result[0], 0.5);
        }
    }

    /**
     * tests the differential equation solvers on the stiff equation y" + 101y' + 100y = 0. This can be split into two
     * equations: y' = p and p' = -100y -101p with y(0) = 1 and p(0) = -1. The exact solution is y(x) = exp(-x). see F. Scheid,
     * Numerical Analysis, 2nd Edition, p.236 (Schaum Outline Series).
     */
    @Test
    public void testODEStiff()
    {
        for (NumericalIntegratorType integrationMethod : NumericalIntegratorType.values())
        {
            double stepSize = 0.01;
            if (integrationMethod == NumericalIntegratorType.MILNE)
            {
                continue; // MILNE does not converge for this ODE...
            }
            if (integrationMethod == NumericalIntegratorType.ADAMS
                    || integrationMethod == NumericalIntegratorType.RUNGEKUTTAFEHLBERG)
            {
                stepSize = 0.0005;
            }
            StiffODE ode = new StiffODE(stepSize, integrationMethod);
            String m = "Method " + integrationMethod.toString() + ", x = ";
            for (double x = 0.0; x <= 10.0; x += 0.5)
            {
                double[] result = ode.y(x);
                double expected = Math.exp(-x);
                assertEquals(m + x, expected, result[0], 0.1 * expected);
            }
        }
    }

    /** ODE. */
    class ODE extends DifferentialEquation
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * constructs a new Function.
         * @param stepSize the stepSize
         * @param integrationMethod the methodOfIntegration
         */
        ODE(final double stepSize, final NumericalIntegratorType integrationMethod)
        {
            super(stepSize, integrationMethod);
            super.initialize(1.0, new double[] {1.0});
        }

        /** {@inheritDoc} */
        @Override
        public double[] dy(final double x, final double[] y)
        {
            return new double[] {x * Math.pow(y[0], 1.0 / 3.0)};
        }

        /** {@inheritDoc} */
        @Override
        public Serializable getSourceId()
        {
            return "ODE";
        }
    }

    /** Stiff ODE. */
    class StiffODE extends DifferentialEquation
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * constructs a new Function.
         * @param stepSize the stepSize
         * @param integrationMethod the methodOfIntegration
         */
        StiffODE(final double stepSize, final NumericalIntegratorType integrationMethod)
        {
            super(stepSize, integrationMethod);
            super.initialize(0, new double[] {1.0, -1.0});
        }

        /** {@inheritDoc} */
        @Override
        public double[] dy(final double x, final double[] y)
        {
            return new double[] {y[1], -100 * y[0] - 101 * y[1]};
        }

        /** {@inheritDoc} */
        @Override
        public Serializable getSourceId()
        {
            return "StiffODE";
        }
    }
}
