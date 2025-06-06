package nl.tudelft.simulation.dsol.simtime.dist;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vfloat.scalar.FloatDuration;

import nl.tudelft.simulation.jstats.distributions.Dist;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;

/**
 * Definitions of distributions over relative time. The distributions wrap a ContinuousDist from the
 * nl.tudelft.simulation.jstats.distributions package in dsol-core.
 * <p>
 * Copyright (c) 2016-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @param <T> the relative time type.
 */
public abstract class DistContinuousSimulationTime<T extends Number & Comparable<T>> extends Dist
{
    /** */
    private static final long serialVersionUID = 20140805L;

    /** the wrapped distribution. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    public DistContinuous wrappedDistribution;

    /**
     * constructs a new continuous distribution.
     * @param wrappedDistribution the wrapped continuous distribution
     */
    public DistContinuousSimulationTime(final DistContinuous wrappedDistribution)
    {
        super(wrappedDistribution.getStream());
        this.wrappedDistribution = wrappedDistribution;
    }

    /**
     * draws the next stream value according to the probability of this this distribution.
     * @return the next double value drawn.
     */
    public abstract T draw();

    /**
     * returns the probability density for a value x.
     * @param x the value for which to calculate the probability density.
     * @return the probability density for value x
     */
    public double probDensity(final double x)
    {
        return this.wrappedDistribution.getProbabilityDensity(x);
    }

    /***********************************************************************************************************/
    /************************************* EASY ACCESS CLASS EXTENSIONS ****************************************/
    /***********************************************************************************************************/

    /** Easy access class DistContinuousTime.Double. */
    public static class TimeDouble extends DistContinuousSimulationTime<Double>
    {
        /** */
        private static final long serialVersionUID = 20140805L;

        /**
         * @param wrappedDistribution the wrapped continuous distribution
         */
        public TimeDouble(final DistContinuous wrappedDistribution)
        {
            super(wrappedDistribution);
        }

        @Override
        public Double draw()
        {
            return super.wrappedDistribution.draw();
        }
    }

    /** Easy access class DistContinuousTime.Float. */
    public static class TimeFloat extends DistContinuousSimulationTime<Float>
    {
        /** */
        private static final long serialVersionUID = 20140805L;

        /**
         * @param wrappedDistribution the wrapped continuous distribution
         */
        public TimeFloat(final DistContinuous wrappedDistribution)
        {
            super(wrappedDistribution);
        }

        @Override
        public Float draw()
        {
            return (float) super.wrappedDistribution.draw();
        }
    }

    /** Easy access class DistContinuousTime.Long. */
    public static class TimeLong extends DistContinuousSimulationTime<Long>
    {
        /** */
        private static final long serialVersionUID = 20140805L;

        /**
         * @param wrappedDistribution the wrapped continuous distribution
         */
        public TimeLong(final DistContinuous wrappedDistribution)
        {
            super(wrappedDistribution);
        }

        @Override
        public Long draw()
        {
            return (long) super.wrappedDistribution.draw();
        }
    }

    /** Easy access class DistContinuousTime.DoubleUnit. */
    public static class TimeDoubleUnit extends DistContinuousSimulationTime<Duration>
    {
        /** */
        private static final long serialVersionUID = 20140805L;

        /** the time unit. */
        private final DurationUnit unit;

        /**
         * @param wrappedDistribution the wrapped continuous distribution
         * @param unit the unit for the parameters (and drawn values) of the wrapped distribution
         */
        public TimeDoubleUnit(final DistContinuous wrappedDistribution, final DurationUnit unit)
        {
            super(wrappedDistribution);
            this.unit = unit;
        }

        @Override
        public Duration draw()
        {
            return new Duration(super.wrappedDistribution.draw(), this.unit);
        }
    }

    /** Easy access class DistContinuousTime.FloatUnit. */
    public static class TimeFloatUnit extends DistContinuousSimulationTime<FloatDuration>
    {
        /** */
        private static final long serialVersionUID = 20140805L;

        /** the time unit. */
        private final DurationUnit unit;

        /**
         * @param wrappedDistribution the wrapped continuous distribution
         * @param unit the unit for the parameters (and drawn values) of the wrapped distribution
         */
        public TimeFloatUnit(final DistContinuous wrappedDistribution, final DurationUnit unit)
        {
            super(wrappedDistribution);
            this.unit = unit;
        }

        @Override
        public FloatDuration draw()
        {
            return new FloatDuration((float) super.wrappedDistribution.draw(), this.unit);
        }
    }

}
