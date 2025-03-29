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
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
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
     * @param key the key for the dataset
     * @param domain the domain of the set.
     * @param range the range of the set.
     * @param numberOfBins the number of bins
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
     * @param name the name of the series.
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
     * @param newSeries the set to add.
     */
    public synchronized void addSeries(final HistogramSeries newSeries)
    {
        newSeries.addChangeListener(this);
        List<HistogramSeries> list = new ArrayList<HistogramSeries>(Arrays.asList(this.series));
        list.add(newSeries);
        this.series = list.toArray(new HistogramSeries[list.size()]);
        this.fireDatasetChanged();
    }

    @Override
    public void datasetChanged(final DatasetChangeEvent arg0)
    {
        if (arg0 != null)
        {
            this.fireDatasetChanged();
        }
    }

    @Override
    public double getEndXValue(final int serieNr, final int bin)
    {
        return this.series[serieNr].getEndXValue(bin);
    }

    @Override
    public double getEndYValue(final int serieNr, final int bin)
    {
        return this.series[serieNr].getEndYValue(bin);
    }

    @Override
    public double getStartXValue(final int serieNr, final int bin)
    {
        return this.series[serieNr].getStartXValue(bin);
    }

    @Override
    public double getStartYValue(final int serieNr, final int bin)
    {
        return this.series[serieNr].getStartYValue(bin);
    }

    @Override
    public int getItemCount(final int serieNr)
    {
        return this.series[serieNr].getBinCount();
    }

    @Override
    public double getXValue(final int serieNr, final int bin)
    {
        return this.series[serieNr].getXValue(bin);
    }

    @Override
    public double getYValue(final int serieNr, final int bin)
    {
        return this.series[serieNr].getYValue(bin);
    }

    @Override
    public int getSeriesCount()
    {
        return this.series.length;
    }

    /**
     * @param serieNr the series number to retrieve the name for
     * @return the series name
     */
    public String getSeriesName(final int serieNr)
    {
        return this.series[serieNr].getName();
    }

    @Override
    public Number getEndX(final int serieNr, final int bin)
    {
        return this.series[serieNr].getEndXValue(bin);
    }

    @Override
    public Number getEndY(final int serieNr, final int bin)
    {
        return this.series[serieNr].getEndYValue(bin);
    }

    @Override
    public Number getStartX(final int serieNr, final int bin)
    {
        return this.series[serieNr].getStartXValue(bin);
    }

    @Override
    public Number getStartY(final int serieNr, final int bin)
    {
        return this.series[serieNr].getStartYValue(bin);
    }

    @Override
    public Number getX(final int serieNr, final int bin)
    {
        return this.series[serieNr].getXValue(bin);
    }

    @Override
    public Number getY(final int serieNr, final int bin)
    {
        return this.series[serieNr].getYValue(bin);
    }
}
