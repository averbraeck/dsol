package nl.tudelft.simulation.dsol.swing.charts.histogram;

import org.djutils.event.EventInterface;
import org.djutils.event.EventListenerInterface;
import org.jfree.data.general.AbstractDataset;

/**
 * The serie defines a histogram series containing the entries of a set.
 * <p>
 * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology </a>, the Netherlands. <br>
 * See for project information <a href="https://simulation.tudelft.nl"> www.simulation.tudelft.nl </a>.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="mailto:a.verbraeck@tudelft.nl"> Alexander Verbraeck </a> <br>
 *         <a href="https://www.linkedin.com/in/peterhmjacobs"> Peter Jacobs </a>
 * @since 1.5
 */
public class HistogramSeries extends AbstractDataset implements EventListenerInterface
{
    /** default UId. */
    private static final long serialVersionUID = 1L;

    /** name refers to the name of the serie. */
    private String name;

    /** bins refers to the bins in this serie. */
    private Bin[] bins = null;

    /**
     * constructs a new HistogramSeries.
     * @param name String; the name of the dataset
     * @param domain double[]; the domain of the serie
     * @param range double[]; the range of the serie
     * @param numberOfBins int; the number of bins to be used
     */
    public HistogramSeries(final String name, final double[] domain, final double[] range, final int numberOfBins)
    {

        super();
        this.name = name;
        this.bins = new Bin[numberOfBins + 2];
        double binWidth = (domain[1] - domain[0]) / numberOfBins * 1.0;
        double min = domain[0] - binWidth;
        for (int i = 0; i < numberOfBins + 2; i++)
        {
            this.bins[i] = new Bin(new double[] {min, min + binWidth}, range);
            if (range != null)
            {
                this.bins[i].setFixed(true);
            }
            min = min + binWidth;
        }
        this.fireDatasetChanged();
    }

    /**
     * returns the name of the series.
     * @return String the name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * returns the maxX value for bin.
     * @param bin int; the bin number
     * @return Number
     */
    public double getEndXValue(final int bin)
    {
        return this.bins[bin].getEndXValue();
    }

    /**
     * returns the maxY value.
     * @param bin int; the bin number
     * @return Number
     */
    public double getEndYValue(final int bin)
    {
        return this.bins[bin].getEndYValue();
    }

    /**
     * returns the minimumX value.
     * @param bin int; the bin number
     * @return Number
     */
    public double getStartXValue(final int bin)
    {
        return this.bins[bin].getStartXValue();
    }

    /**
     * returns the minimumY value.
     * @param bin int; the bin number
     * @return Number
     */
    public double getStartYValue(final int bin)
    {
        return this.bins[bin].getStartYValue();
    }

    /**
     * returns the number of bins in the histogram.
     * @return int
     */
    public int getBinCount()
    {
        return this.bins.length;
    }

    /**
     * returns the x value.
     * @param bin int; the bin number
     * @return Number
     */
    public double getXValue(final int bin)
    {
        return this.bins[bin].getXValue();
    }

    /**
     * returns the Y value.
     * @param bin int; the bin number
     * @return Number
     */
    public int getYValue(final int bin)
    {
        return this.bins[bin].getYValue();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void notify(final EventInterface event)
    {
        double value = ((Double) event.getContent()).doubleValue();
        this.bins[this.resolveBin(value)].increase();
        this.fireDatasetChanged();
    }

    /**
     * resolves the bin for a particular value.
     * @param value double; the value of the incomming event
     * @return int the bin number
     */
    private int resolveBin(final double value)
    {
        if (value <= this.bins[1].getStartXValue())
        {
            return 0;
        }
        for (int i = 1; i < this.bins.length - 1; i++)
        {
            if (value > this.bins[i].getStartXValue() && value <= this.bins[i].getEndXValue())
            {
                return i;
            }
        }
        return this.bins.length - 1;
    }

    /**
     * defines the bins in the histogram.
     */
    private class Bin
    {
        /** the domain of the bin. */
        private double[] domain;

        /** the range of the bin. */
        private double[] range = null;

        /** observations refers to the value of this bin. */
        private int observations = 0;

        /** fixed refers to autoscaling versus fixed scaling. */
        private boolean fixed = false;

        /**
         * constructs a new Bin.
         * @param domain double[]; the domain of the bin
         * @param range double[]; the range of the bin
         */
        Bin(final double[] domain, final double[] range)
        {
            this.domain = domain;
            if (range == null)
            {
                this.range = new double[] {0, 1};
            }
            else
            {
                this.range = range;
                this.fixed = true;
            }
        }

        /**
         * increases the value of the bin with 1.
         */
        public void increase()
        {
            this.observations++;
            if (!this.fixed && this.observations >= this.range[1])
            {
                this.range[1] = this.observations;
            }
        }

        /**
         * returns the minimum x value.
         * @return Number
         */
        public double getStartXValue()
        {
            return this.domain[0];
        }

        /**
         * returns the minimum y value.
         * @return Number
         */
        public double getStartYValue()
        {
            return this.range[0];
        }

        /**
         * returns the maximum X value.
         * @return Number
         */
        public double getEndXValue()
        {
            return this.domain[1];
        }

        /**
         * returns the maximum Y value.
         * @return Number
         */
        public double getEndYValue()
        {
            return this.range[1];
        }

        /**
         * returns the x value.
         * @return Number
         */
        public synchronized double getXValue()
        {
            return 0.5 * (this.domain[1] - this.domain[0]);
        }

        /**
         * returns the y value.
         * @return Number
         */
        public int getYValue()
        {
            return this.observations;
        }

        /**
         * sets the fixed attributed.
         * @param fixed boolean; is the bin fixed in range
         */
        public void setFixed(final boolean fixed)
        {
            this.fixed = fixed;
        }

    }
}
