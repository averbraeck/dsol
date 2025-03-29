package nl.tudelft.simulation.dsol.swing.charts.xy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jfree.data.DomainOrder;
import org.jfree.data.general.AbstractSeriesDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;

import nl.tudelft.simulation.language.filters.FilterInterface;

/**
 * The xyDataset specifies the xyDataset in DSOL.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @since 1.5
 */
public class XYDataset extends AbstractSeriesDataset implements org.jfree.data.xy.XYDataset, DatasetChangeListener
{

    /** default UId. */
    private static final long serialVersionUID = 1L;

    /** series contains the series of the set. */
    private XYSeries[] series = new XYSeries[0];

    /**
     * constructs a new XYDataset.
     */
    public XYDataset()
    {
        super();
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

    /**
     * adds a dataset to the series
     * @param dataset the set
     */
    public synchronized void addSeries(final XYSeries dataset)
    {
        dataset.addChangeListener(this);
        List<XYSeries> list = new ArrayList<XYSeries>(Arrays.asList(this.series));
        list.add(dataset);
        this.series = list.toArray(new XYSeries[list.size()]);
        this.fireDatasetChanged();
    }

    @Override
    public DomainOrder getDomainOrder()
    {
        return DomainOrder.ASCENDING;
    }

    @Override
    public int getItemCount(final int serie)
    {
        return this.series[serie].getItemCount();
    }

    @Override
    public double getXValue(final int serie, final int item)
    {
        return this.series[serie].getXValue(item);
    }

    @Override
    public double getYValue(final int serie, final int item)
    {
        return this.series[serie].getYValue(item);
    }

    @Override
    public int getSeriesCount()
    {
        return this.series.length;
    }

    /**
     * @param serie the series to retrieve the name for
     * @return name the name of the series
     */
    public String getSeriesName(final int serie)
    {
        return this.series[serie].getSeriesName();
    }

    @Override
    public Number getX(final int serie, final int item)
    {
        return Double.valueOf(this.series[serie].getXValue(item));
    }

    @Override
    public Number getY(final int serie, final int item)
    {
        return Double.valueOf(this.series[serie].getYValue(item));
    }

    /**
     * applies a filter on the chart
     * @param filter the filter to apply
     */
    public void setFilter(final FilterInterface filter)
    {
        for (int i = 0; i < this.series.length; i++)
        {
            this.series[i].setFilter(filter);
        }
    }

    @Override
    public Comparable<?> getSeriesKey(final int seriesNumber)
    {
        return this.series[seriesNumber].getSeriesName();
    }

}
