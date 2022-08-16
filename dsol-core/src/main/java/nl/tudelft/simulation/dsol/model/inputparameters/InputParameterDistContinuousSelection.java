package nl.tudelft.simulation.dsol.model.inputparameters;

import java.util.SortedMap;
import java.util.TreeMap;

import org.djutils.exceptions.Throw;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.jstats.distributions.DistBeta;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistErlang;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.distributions.DistGamma;
import nl.tudelft.simulation.jstats.distributions.DistLogNormal;
import nl.tudelft.simulation.jstats.distributions.DistNormal;
import nl.tudelft.simulation.jstats.distributions.DistPearson5;
import nl.tudelft.simulation.jstats.distributions.DistPearson6;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.jstats.distributions.DistUniform;
import nl.tudelft.simulation.jstats.distributions.DistWeibull;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * InputParameterDistContinuousSelection takes care of exposing the necessary parameters for each of the continuous distribution
 * functions. It has a function called getDist() which returns the distribution of the current choice. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class InputParameterDistContinuousSelection extends InputParameterSelectionMap<String, InputParameterMapDistContinuous>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the options for the distribution functions. */
    private static SortedMap<String, InputParameterMapDistContinuous> distOptions;

    /** the random number stream to use for the distribution. */
    private StreamInterface stream;

    /** Make the map with distribution function options. */
    static
    {
        distOptions = new TreeMap<>();
        try
        {
            distOptions.put("Beta", new Beta());
            distOptions.put("Constant", new Constant());
            distOptions.put("Erlang", new Erlang());
            distOptions.put("Exponential", new Exponential());
            distOptions.put("Gamma", new Gamma());
            distOptions.put("LogNormal", new LogNormal());
            distOptions.put("Normal", new Normal());
            distOptions.put("Pearson5", new Pearson5());
            distOptions.put("Pearson6", new Pearson6());
            distOptions.put("Triangular", new Triangular());
            distOptions.put("Uniform", new Uniform());
            distOptions.put("Weibull", new Weibull());
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
    public InputParameterDistContinuousSelection(final String key, final String shortName, final String description,
            final StreamInterface stream, final double displayPriority) throws InputParameterException
    {
        super(key, shortName, description, distOptions, distOptions.get("Exponential"), displayPriority);
        Throw.whenNull(stream, "stream cannot be null");
        this.stream = stream;
        for (InputParameterMapDistContinuous dist : getOptions().values())
        {
            dist.setStream(this.stream);
        }
    }

    /**
     * Return the distribution function corresponding to the chosen distribution and parameters.
     * @return DistContinuous; the distribution function corresponding to the chosen distribution and parameters
     * @throws InputParameterException on error retrieving the values for the distribution
     */
    public DistContinuous getDist() throws InputParameterException
    {
        return getValue().getDist();
    }

    /**
     * @return distOptions
     */
    public static final SortedMap<String, InputParameterMapDistContinuous> getDistOptions()
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

    /** InputParameterDistContinuous.Beta class. */
    public static class Beta extends InputParameterMapDistContinuous
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct the input for the Beta distribution.
         * @throws InputParameterException on error with the distribution
         */
        public Beta() throws InputParameterException
        {
            super("Beta", "Beta", "Beta distribution", 1.0);
            add(new InputParameterDouble("alpha1", "alpha1", "alpha1 value", 1.0, 0.0, Double.MAX_VALUE, false, false, "%f",
                    1.0));
            add(new InputParameterDouble("alpha2", "alpha2", "alpha2 value", 1.0, 0.0, Double.MAX_VALUE, false, false, "%f",
                    2.0));
        }

        /** {@inheritDoc} */
        @Override
        public void setDist() throws InputParameterException
        {
            Throw.when((Double) get("alpha1").getValue() <= 0.0, InputParameterException.class, "DistBeta: alpha1 <= 0.0");
            Throw.when((Double) get("alpha2").getValue() <= 0.0, InputParameterException.class, "DistBeta: alpha2 <= 0.0");
            this.dist = new DistBeta(getStream(), (Double) get("alpha1").getValue(), (Double) get("alpha2").getValue());
        }
    }

    /** InputParameterDistContinuous.Constant class. */
    public static class Constant extends InputParameterMapDistContinuous
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct the input for the Constant distribution.
         * @throws InputParameterException on error with the distribution
         */
        public Constant() throws InputParameterException
        {
            super("Constant", "Constant", "Constant 'distribution'", 1.0);
            add(new InputParameterDouble("c", "c", "constant value to return", 0.0, -Double.MAX_VALUE, Double.MAX_VALUE, false,
                    false, "%f", 1.0));
        }

        /** {@inheritDoc} */
        @Override
        public void setDist() throws InputParameterException
        {
            this.dist = new DistConstant(getStream(), (Double) get("c").getValue());
        }
    }

    /** InputParameterDistContinuous.Erlang class. */
    public static class Erlang extends InputParameterMapDistContinuous
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct the input for the Erlang distribution.
         * @throws InputParameterException on error with the distribution
         */
        public Erlang() throws InputParameterException
        {
            super("Erlang", "Erlang", "Erlang distribution", 1.0);
            add(new InputParameterDouble("scale", "scale", "scale value, mean of the exponential distribution", 1.0, 0.0,
                    Double.MAX_VALUE, false, false, "%f", 1.0));
            add(new InputParameterInteger("k", "k", "k value, number of subsequent exponential processes", 1, 0,
                    Integer.MAX_VALUE, "%f", 1.0));
        }

        /** {@inheritDoc} */
        @Override
        public void setDist() throws InputParameterException
        {
            Throw.when((Double) get("scale").getValue() <= 0.0, InputParameterException.class, "DistErlang: scale <= 0.0");
            Throw.when((Integer) get("k").getValue() <= 0, InputParameterException.class, "DistErlang: k <= 0");
            this.dist = new DistErlang(getStream(), (Double) get("scale").getValue(), (Integer) get("k").getValue());
        }
    }

    /** InputParameterDistContinuous.Exponential class. */
    public static class Exponential extends InputParameterMapDistContinuous
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct the input for the Exponential distribution.
         * @throws InputParameterException on error with the distribution
         */
        public Exponential() throws InputParameterException
        {
            super("Exponential", "Exponential", "Negative Exponential distribution", 1.0);
            add(new InputParameterDouble("lambda", "lambda", "lambda value, mean of the exponential distribution", 1.0, 0.0,
                    Double.MAX_VALUE, false, false, "%f", 1.0));
        }

        /** {@inheritDoc} */
        @Override
        public void setDist() throws InputParameterException
        {
            Throw.when((Double) get("lambda").getValue() <= 0.0, InputParameterException.class,
                    "DistExponential: lambda <= 0.0");
            this.dist = new DistExponential(getStream(), (Double) get("lambda").getValue());
        }
    }

    /** InputParameterDistContinuous.Gamma class. */
    public static class Gamma extends InputParameterMapDistContinuous
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct the input for the Gamma distribution.
         * @throws InputParameterException on error with the distribution
         */
        public Gamma() throws InputParameterException
        {
            super("Gamma", "Gamma", "Gamma distribution", 1.0);
            add(new InputParameterDouble("shape", "shape", "shape (alpha) value", 1.0, 0.0, Double.MAX_VALUE, false, false,
                    "%f", 1.0));
            add(new InputParameterDouble("scale", "scale", "scale (beta) value", 1.0, 0.0, Double.MAX_VALUE, false, false, "%f",
                    2.0));
        }

        /** {@inheritDoc} */
        @Override
        public void setDist() throws InputParameterException
        {
            Throw.when((Double) get("shape").getValue() <= 0.0, InputParameterException.class, "DistGamma: shape <= 0.0");
            Throw.when((Double) get("scale").getValue() <= 0.0, InputParameterException.class, "DistGamma: scale <= 0.0");
            this.dist = new DistGamma(getStream(), (Double) get("shape").getValue(), (Double) get("scale").getValue());
        }
    }

    /** InputParameterDistContinuous.LogNormal class. */
    public static class LogNormal extends InputParameterMapDistContinuous
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct the input for the LogNormal distribution.
         * @throws InputParameterException on error with the distribution
         */
        public LogNormal() throws InputParameterException
        {
            super("LogNormal", "LogNormal", "LogNormal distribution", 1.0);
            add(new InputParameterDouble("mu", "mu", "mu value, mean of the underlying Normal distribution", 0.0,
                    -Double.MAX_VALUE, Double.MAX_VALUE, false, false, "%f", 1.0));
            add(new InputParameterDouble("sigma", "sigma",
                    "sigma value, standard deviation of the underlying Normal distribution", 1.0, 0.0, Double.MAX_VALUE, false,
                    false, "%f", 2.0));
        }

        /** {@inheritDoc} */
        @Override
        public void setDist() throws InputParameterException
        {
            Throw.when((Double) get("sigma").getValue() <= 0.0, InputParameterException.class, "DistLogNormal: sigma <= 0.0");
            this.dist = new DistLogNormal(getStream(), (Double) get("mu").getValue(), (Double) get("sigma").getValue());
        }
    }

    /** InputParameterDistContinuous.Normal class. */
    public static class Normal extends InputParameterMapDistContinuous
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct the input for the Normal distribution.
         * @throws InputParameterException on error with the distribution
         */
        public Normal() throws InputParameterException
        {
            super("Normal", "Normal", "Normal distribution", 1.0);
            add(new InputParameterDouble("mu", "mu", "mu value, mean of the Normal distribution", 0.0, -Double.MAX_VALUE,
                    Double.MAX_VALUE, false, false, "%f", 1.0));
            add(new InputParameterDouble("sigma", "sigma", "sigma value, standard deviation of the Normal distribution", 1.0,
                    0.0, Double.MAX_VALUE, true, false, "%f", 2.0));
        }

        /** {@inheritDoc} */
        @Override
        public void setDist() throws InputParameterException
        {
            Throw.when((Double) get("sigma").getValue() < 0.0, InputParameterException.class, "DistNormal: sigma < 0.0");
            this.dist = new DistNormal(getStream(), (Double) get("mu").getValue(), (Double) get("sigma").getValue());
        }
    }

    /** InputParameterDistContinuous.Pearson5 class. */
    public static class Pearson5 extends InputParameterMapDistContinuous
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct the input for the Pearson5 distribution.
         * @throws InputParameterException on error with the distribution
         */
        public Pearson5() throws InputParameterException
        {
            super("Pearson5", "Pearson5", "Pearson5 distribution", 1.0);
            add(new InputParameterDouble("alpha", "alpha", "alpha value", 1.0, 0.0, Double.MAX_VALUE, false, false, "%f", 1.0));
            add(new InputParameterDouble("beta", "beta", "beta value", 1.0, 0.0, Double.MAX_VALUE, false, false, "%f", 2.0));
        }

        /** {@inheritDoc} */
        @Override
        public void setDist() throws InputParameterException
        {
            Throw.when((Double) get("alpha").getValue() <= 0.0, InputParameterException.class, "DistPearson5: alpha <= 0.0");
            Throw.when((Double) get("beta").getValue() <= 0.0, InputParameterException.class, "DistPearson5: beta <= 0.0");
            this.dist = new DistPearson5(getStream(), (Double) get("alpha").getValue(), (Double) get("beta").getValue());
        }
    }

    /** InputParameterDistContinuous.Pearson6 class. */
    public static class Pearson6 extends InputParameterMapDistContinuous
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct the input for the Pearson6 distribution.
         * @throws InputParameterException on error with the distribution
         */
        public Pearson6() throws InputParameterException
        {
            super("Pearson6", "Pearson6", "Pearson6 distribution", 1.0);
            add(new InputParameterDouble("alpha1", "alpha1", "alpha1 value", 1.0, 0.0, Double.MAX_VALUE, false, false, "%f",
                    1.0));
            add(new InputParameterDouble("alpha2", "alpha2", "alpha2 value", 1.0, 0.0, Double.MAX_VALUE, false, false, "%f",
                    2.0));
            add(new InputParameterDouble("beta", "beta", "beta value", 1.0, 0.0, Double.MAX_VALUE, false, false, "%f", 3.0));
        }

        /** {@inheritDoc} */
        @Override
        public void setDist() throws InputParameterException
        {
            Throw.when((Double) get("alpha1").getValue() <= 0.0, InputParameterException.class, "DistPearson6: alpha1 <= 0.0");
            Throw.when((Double) get("alpha2").getValue() <= 0.0, InputParameterException.class, "DistPearson6: alpha2 <= 0.0");
            Throw.when((Double) get("beta").getValue() <= 0.0, InputParameterException.class, "DistPearson6: beta <= 0.0");
            this.dist = new DistPearson6(getStream(), (Double) get("alpha1").getValue(), (Double) get("alpha2").getValue(),
                    (Double) get("beta").getValue());
        }
    }

    /** InputParameterDistContinuous.Triangular class. */
    public static class Triangular extends InputParameterMapDistContinuous
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct the input for the Triangular distribution.
         * @throws InputParameterException on error with the distribution
         */
        public Triangular() throws InputParameterException
        {
            super("Triangular", "Triangular", "Triangular distribution", 1.0);
            add(new InputParameterDouble("min", "min", "minimum value, lowest value of the Triangular distribution", 0.0,
                    -Double.MAX_VALUE, Double.MAX_VALUE, false, false, "%f", 1.0));
            add(new InputParameterDouble("mode", "mode", "mode value of the Triangular distribution", 1.0, -Double.MAX_VALUE,
                    Double.MAX_VALUE, false, false, "%f", 2.0));
            add(new InputParameterDouble("max", "max", "max value, highest value of the Triangular distribution", 2.0,
                    -Double.MAX_VALUE, Double.MAX_VALUE, false, false, "%f", 3.0));
        }

        /** {@inheritDoc} */
        @Override
        public void setDist() throws InputParameterException
        {
            Throw.when((Double) get("min").getValue() > (Double) get("mode").getValue(), InputParameterException.class,
                    "DistTriangular: min > mode");
            Throw.when((Double) get("mode").getValue() > (Double) get("max").getValue(), InputParameterException.class,
                    "DistTriangular: mode > max");
            Throw.when(
                    ((Double) get("min").getValue()).equals(get("mode").getValue())
                            && ((Double) get("mode").getValue()).equals(get("max").getValue()),
                    InputParameterException.class, "DistTriangular: min == mode == max");
            this.dist = new DistTriangular(getStream(), (Double) get("min").getValue(), (Double) get("mode").getValue(),
                    (Double) get("max").getValue());
        }
    }

    /** InputParameterDistContinuous.Uniform class. */
    public static class Uniform extends InputParameterMapDistContinuous
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct the input for the Uniform distribution.
         * @throws InputParameterException on error with the distribution
         */
        public Uniform() throws InputParameterException
        {
            super("Uniform", "Uniform", "Uniform distribution", 1.0);
            add(new InputParameterDouble("min", "min", "min value, lowest value of the Uniform distribution", 0.0,
                    -Double.MAX_VALUE, Double.MAX_VALUE, false, false, "%f", 1.0));
            add(new InputParameterDouble("max", "max", "max value, highest value of the Uniform distribution", 1.0,
                    -Double.MAX_VALUE, Double.MAX_VALUE, false, false, "%f", 2.0));
        }

        /** {@inheritDoc} */
        @Override
        public void setDist() throws InputParameterException
        {
            Throw.when((Double) get("min").getValue() >= (Double) get("max").getValue(), InputParameterException.class,
                    "DistUniform: a >= b");
            this.dist = new DistUniform(getStream(), (Double) get("min").getValue(), (Double) get("max").getValue());
        }
    }

    /** InputParameterDistContinuous.Weibull class. */
    public static class Weibull extends InputParameterMapDistContinuous
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct the input for the Weibull distribution.
         * @throws InputParameterException on error with the distribution
         */
        public Weibull() throws InputParameterException
        {
            super("Weibull", "Weibull", "Weibull distribution", 1.0);
            add(new InputParameterDouble("alpha", "alpha", "alpha value", 1.0, 0.0, Double.MAX_VALUE, false, false, "%f", 1.0));
            add(new InputParameterDouble("beta", "beta", "beta value", 1.0, 0.0, Double.MAX_VALUE, false, false, "%f", 2.0));
        }

        /** {@inheritDoc} */
        @Override
        public void setDist() throws InputParameterException
        {
            Throw.when((Double) get("alpha").getValue() <= 0.0, InputParameterException.class, "DistWeibull: alpha <= 0.0");
            Throw.when((Double) get("beta").getValue() <= 0.0, InputParameterException.class, "DistWeibull: beta <= 0.0");
            this.dist = new DistWeibull(getStream(), (Double) get("alpha").getValue(), (Double) get("beta").getValue());
        }
    }

}
