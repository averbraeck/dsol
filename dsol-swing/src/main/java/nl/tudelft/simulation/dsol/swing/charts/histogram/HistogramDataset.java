package nl.tudelft.simulation.dsol.swing.charts.histogram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.statistics.SimpleHistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;

/**
 * The dataset defines a histogram data set. A dataset contains multiple series each containing the entries to display.
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
public class HistogramDataset extends SimpleHistogramDataset implements IntervalXYDataset, DatasetChangeListener
{
    /** */
    private static final long serialVersionUID = 1L;

    /** domain is the minimal value to be displayed in this set. */
    protected double[] domain = null;

    /** range is the maximum value to be displayed in the set. */
    protected double[] range = null;

    /** numberOfBins is the number of bins (or categories between min-max) */
    protected int numberOfBins = 0;

    /** series the series in this set. */
    protected HistogramSeries[] series = new HistogramSeries[0];

    /**
     * constructs a new HistogramDataset.
     * @param key Comparable&lt;?&gt;; the key for the dataset
     * @param domain double[]; the domain of the set.
     * @param range double[]; the range of the set.
     * @param numberOfBins int; the number of bins
     */
    public HistogramDataset(final Comparable<?> key, final double[] domain, final double[] range, final int numberOfBins)
    {
        super(key);
        this.domain = domain;
        this.range = range;
        this.numberOfBins = numberOfBins;
    }

    /**
     * adds a series to the dataset.
     * @param name String; the name of the series.
     * @return HistogramSeries.
     */
    public synchronized HistogramSeries addSeries(final String name)
    {
        HistogramSeries histogramSeries = new HistogramSeries(name, this.domain, this.range, this.numberOfBins);
        this.addSeries(histogramSeries);
        return histogramSeries;
    }

    /**
     * adds a series to the dataset
     * @param newSeries HistogramSeries; the set to add.
     */
    public synchronized void addSeries(final HistogramSeries newSeries)
    {
        newSeries.addChangeListener(this);
        List<HistogramSeries> list = new ArrayList<HistogramSeries>(Arrays.asList(this.series));
        list.add(newSeries);
        this.series = list.toArray(new HistogramSeries[list.size()]);
        this.fireDatasetChanged();
    }

    /** {@inheritDoc} */
    @Override
    public void datasetChanged(final DatasetChangeEvent arg0)
    {
        if (arg0 != null)
        {
            this.fireDatasetChanged();
        }
    }

    /** {@inheritDoc} */
    @Override
    public double getEndXValue(final int serieNr, final int bin)
    {
        return this.series[serieNr].getEndXValue(bin);
    }

    /** {@inheritDoc} */
    @Override
    public double getEndYValue(final int serieNr, final int bin)
    {
        return this.series[serieNr].getEndYValue(bin);
    }

    /** {@inheritDoc} */
    @Override
    public double getStartXValue(final int serieNr, final int bin)
    {
        return this.series[serieNr].getStartXValue(bin);
    }

    /** {@inheritDoc} */
    @Override
    public double getStartYValue(final int serieNr, final int bin)
    {
        return this.series[serieNr].getStartYValue(bin);
    }

    /** {@inheritDoc} */
    @Override
    public int getItemCount(final int serieNr)
    {
        return this.series[serieNr].getBinCount();
    }

    /** {@inheritDoc} */
    @Override
    public double getXValue(final int serieNr, final int bin)
    {
        return this.series[serieNr].getXValue(bin);
    }

    /** {@inheritDoc} */
    @Override
    public double getYValue(final int serieNr, final int bin)
    {
        return this.series[serieNr].getYValue(bin);
    }

    /** {@inheritDoc} */
    @Override
    public int getSeriesCount()
    {
        return this.series.length;
    }

    /**
     * @param serieNr int; the series number to retrieve the name for
     * @return the series name
     */
    public String getSeriesName(final int serieNr)
    {
        return this.series[serieNr].getName();
    }

    /** {@inheritDoc} */
    @Override
    public Number getEndX(final int serieNr, final int bin)
    {
        return this.series[serieNr].getEndXValue(bin);
    }

    /** {@inheritDoc} */
    @Override
    public Number getEndY(final int serieNr, final int bin)
    {
        return this.series[serieNr].getEndYValue(bin);
    }

    /** {@inheritDoc} */
    @Override
    public Number getStartX(final int serieNr, final int bin)
    {
        return this.series[serieNr].getStartXValue(bin);
    }

    /** {@inheritDoc} */
    @Override
    public Number getStartY(final int serieNr, final int bin)
    {
        return this.series[serieNr].getStartYValue(bin);
    }

    /** {@inheritDoc} */
    @Override
    public Number getX(final int serieNr, final int bin)
    {
        return this.series[serieNr].getXValue(bin);
    }

    /** {@inheritDoc} */
    @Override
    public Number getY(final int serieNr, final int bin)
    {
        return this.series[serieNr].getYValue(bin);
    }
}
