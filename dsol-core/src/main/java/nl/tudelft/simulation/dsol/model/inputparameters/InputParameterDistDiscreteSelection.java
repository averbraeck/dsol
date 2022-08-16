package nl.tudelft.simulation.dsol.model.inputparameters;

import java.util.SortedMap;
import java.util.TreeMap;

import org.djutils.exceptions.Throw;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.jstats.distributions.DistBernoulli;
import nl.tudelft.simulation.jstats.distributions.DistBinomial;
import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteConstant;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteUniform;
import nl.tudelft.simulation.jstats.distributions.DistGeometric;
import nl.tudelft.simulation.jstats.distributions.DistNegBinomial;
import nl.tudelft.simulation.jstats.distributions.DistPoisson;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * InputParameterDistDiscreteSelection takes care of exposing the necessary parameters for each of the discrete distribution
 * functions. It has a function called getDist() which returns the distribution of the current choice. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InputParameterDistDiscreteSelection extends InputParameterSelectionMap<String, InputParameterMapDistDiscrete>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the options for the distribution functions. */
    private static SortedMap<String, InputParameterMapDistDiscrete> distOptions;

    /** the random number stream to use for the distribution. */
    private StreamInterface stream;

    /** Make the map with distribution function options. */
    static
    {
        distOptions = new TreeMap<>();
        try
        {
            distOptions.put("Bernoulli", new Bernoulli());
            distOptions.put("Binomial", new Binomial());
            distOptions.put("DiscreteConstant", new DiscreteConstant());
            distOptions.put("DiscreteUniform", new DiscreteUniform());
            distOptions.put("Geometric", new Geometric());
            distOptions.put("NegBinomial", new NegBinomial());
            distOptions.put("Poisson", new Poisson());
        }
        catch (InputParameterException exception)
        {
            CategoryLogger.always().error(exception);
        }
    }

    /**
     * @param key String; unique name for the selection parameter of the distribution function
     * @param shortName String; concise description of the input parameter
     * @param description String; long description of the input parameter (may use HTML markup)
     * @param stream StreamInterface; the random number stream to use for the distribution
     * @param displayPriority double; sorting order when properties are displayed to the user
     * @throws NullPointerException when key, shortName, defaultValue, description, or stream is null
     * @throws IllegalArgumentException when displayPriority is NaN
     * @throws InputParameterException in case the default value is not part of the list; should not happen
     */
    public InputParameterDistDiscreteSelection(final String key, final String shortName, final String description,
            final StreamInterface stream, final double displayPriority) throws InputParameterException
    {
        super(key, shortName, description, distOptions, distOptions.get("DiscreteConstant"), displayPriority);
        Throw.whenNull(stream, "stream cannot be null");
        this.stream = stream;
        for (InputParameterMapDistDiscrete dist : getOptions().values())
        {
            dist.setStream(stream);
        }
    }

    /**
     * Return the distribution function corresponding to the chosen distribution and parameters.
     * @return DistDiscrete; the distribution function corresponding to the chosen distribution and parameters
     * @throws InputParameterException on error retrieving the values for the distribution
     */
    public DistDiscrete getDist() throws InputParameterException
    {
        return getValue().getDist();
    }

    /**
     * @return distOptions
     */
    public static final SortedMap<String, InputParameterMapDistDiscrete> getDistOptions()
    {
        return distOptions;
    }

    /**
     * @return stream
     */
    public StreamInterface getStream()
    {
        return this.stream;
    }

    /***********************************************************************************************************/
    /*************************************** DISTRIBUTION FUNCTIONS ********************************************/
    /***********************************************************************************************************/

    /** InputParameterDistDiscrete.Bernoulli class. */
    public static class Bernoulli extends InputParameterMapDistDiscrete
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct the input for the Bernoulli distribution.
         * @throws InputParameterException on error with the distribution
         */
        public Bernoulli() throws InputParameterException
        {
            super("Bernoulli", "Bernoulli", "Bernoulli distribution", 1.0);
            add(new InputParameterDouble("p", "p", "p value, chance of success for the Bernoulli distribution", 0.5, 0.0, 1.0,
                    false, false, "%f", 1.0));
        }

        /** {@inheritDoc} */
        @Override
        public void setDist() throws InputParameterException
        {
            Throw.when((Double) get("p").getValue() <= 0.0, InputParameterException.class, "DistBernoulli: p <= 0.0");
            Throw.when((Double) get("p").getValue() >= 1.0, InputParameterException.class, "DistBernoulli: p >= 1.0");
            this.dist = new DistBernoulli(getStream(), (Double) get("p").getValue());
        }
    }

    /** InputParameterDistDiscrete.Binomial class. */
    public static class Binomial extends InputParameterMapDistDiscrete
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct the input for the Binomial distribution.
         * @throws InputParameterException on error with the distribution
         */
        public Binomial() throws InputParameterException
        {
            super("Binomial", "Binomial",
                    "Binomial distribution (Number of successes in n independent Bernoulli trials with probability p)", 1.0);
            add(new InputParameterInteger("n", "n", "n value, number of independent trials", 1, 1, Integer.MAX_VALUE, "%f",
                    1.0));
            add(new InputParameterDouble("p", "p", "p value, probability of success in each trial", 0.5, 0.0, 1.0, false, false,
                    "%f", 2.0));
        }

        /** {@inheritDoc} */
        @Override
        public void setDist() throws InputParameterException
        {
            Throw.when((Integer) get("n").getValue() <= 0, InputParameterException.class, "DistBinomial: n <= 0");
            Throw.when((Double) get("p").getValue() <= 0.0, InputParameterException.class, "DistBinomial: p <= 0.0");
            Throw.when((Double) get("p").getValue() >= 1.0, InputParameterException.class, "DistBinomial: p >= 1.0");
            this.dist = new DistBinomial(getStream(), (Integer) get("n").getValue(), (Double) get("p").getValue());
        }
    }

    /** InputParameterDistDiscrete.DiscreteConstant class. */
    public static class DiscreteConstant extends InputParameterMapDistDiscrete
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct the input for the DiscreteConstant distribution.
         * @throws InputParameterException on error with the distribution
         */
        public DiscreteConstant() throws InputParameterException
        {
            super("DiscreteConstant", "DiscreteConstant", "DiscreteConstant 'distribution'", 1.0);
            add(new InputParameterLong("c", "c", "constant value to return", 0L, Long.MIN_VALUE, Long.MAX_VALUE, "%f", 1.0));
        }

        /** {@inheritDoc} */
        @Override
        public void setDist() throws InputParameterException
        {
            this.dist = new DistDiscreteConstant(getStream(), (Long) get("c").getValue());
        }
    }

    /** InputParameterDistDiscrete.DiscreteUniform class. */
    public static class DiscreteUniform extends InputParameterMapDistDiscrete
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct the input for the DiscreteUniform distribution.
         * @throws InputParameterException on error with the distribution
         */
        public DiscreteUniform() throws InputParameterException
        {
            super("DiscreteUniform", "DiscreteUniform", "DiscreteUniform distribution", 1.0);
            add(new InputParameterLong("min", "min", "minimum value to return (inclusive)", 0L, Long.MIN_VALUE, Long.MAX_VALUE,
                    "%f", 1.0));
            add(new InputParameterLong("max", "max", "maximum value to return (inclusive)", 0L, Long.MIN_VALUE, Long.MAX_VALUE,
                    "%f", 2.0));
        }

        /** {@inheritDoc} */
        @Override
        public void setDist() throws InputParameterException
        {
            Throw.when((Long) get("min").getValue() >= (Long) get("max").getValue(), InputParameterException.class,
                    "DistDiscreteUniform: min value >= max value");
            this.dist = new DistDiscreteUniform(getStream(), (Long) get("min").getValue(), (Long) get("max").getValue());
        }
    }

    /** InputParameterDistDiscrete.Geometric class. */
    public static class Geometric extends InputParameterMapDistDiscrete
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct the input for the Geometric distribution.
         * @throws InputParameterException on error with the distribution
         */
        public Geometric() throws InputParameterException
        {
            super("Geometric", "Geometric", "Geometric distribution", 1.0);
            add(new InputParameterDouble("p", "p", "p value, chance of success for the Geometric distribution", 0.5, 0.0, 1.0,
                    false, false, "%f", 1.0));
        }

        /** {@inheritDoc} */
        @Override
        public void setDist() throws InputParameterException
        {
            Throw.when((Double) get("p").getValue() <= 0.0, InputParameterException.class, "DistGeometric: p <= 0.0");
            Throw.when((Double) get("p").getValue() >= 1.0, InputParameterException.class, "DistGeometric: p >= 1.0");
            this.dist = new DistGeometric(getStream(), (Double) get("p").getValue());
        }
    }

    /** InputParameterDistDiscrete.NegBinomial class. */
    public static class NegBinomial extends InputParameterMapDistDiscrete
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct the input for the NegBinomial distribution.
         * @throws InputParameterException on error with the distribution
         */
        public NegBinomial() throws InputParameterException
        {
            super("NegBinomial", "NegBinomial",
                    "NegBinomial distribution (Number of successes in n independent Bernoulli trials with probability p)", 1.0);
            add(new InputParameterInteger("n", "n", "n value, first success at the n-th trial", 1, 1, Integer.MAX_VALUE, "%f",
                    1.0));
            add(new InputParameterDouble("p", "p", "p value, probability of success in each trial", 0.5, 0.0, 1.0, false, false,
                    "%f", 2.0));
        }

        /** {@inheritDoc} */
        @Override
        public void setDist() throws InputParameterException
        {
            Throw.when((Integer) get("n").getValue() <= 0, InputParameterException.class, "DistNegBinomial: n <= 0");
            Throw.when((Double) get("p").getValue() <= 0.0, InputParameterException.class, "DistNegBinomial: p <= 0.0");
            Throw.when((Double) get("p").getValue() >= 1.0, InputParameterException.class, "DistNegBinomial: p >= 1.0");
            this.dist = new DistNegBinomial(getStream(), (Integer) get("n").getValue(), (Double) get("p").getValue());
        }
    }

    /** InputParameterDistDiscrete.Poisson class. */
    public static class Poisson extends InputParameterMapDistDiscrete
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct the input for the Poisson distribution.
         * @throws InputParameterException on error with the distribution
         */
        public Poisson() throws InputParameterException
        {
            super("Poisson", "Poisson", "Poisson distribution", 1.0);
            add(new InputParameterDouble("lambda", "lambda", "lambda value, mean value", 1.0, 0.0, Double.MAX_VALUE, false,
                    false, "%f", 1.0));
        }

        /** {@inheritDoc} */
        @Override
        public void setDist() throws InputParameterException
        {
            Throw.when((Double) get("lambda").getValue() <= 0.0, InputParameterException.class, "DistPoisson: lambda <= 0.0");
            this.dist = new DistPoisson(getStream(), (Double) get("lambda").getValue());
        }
    }
}
