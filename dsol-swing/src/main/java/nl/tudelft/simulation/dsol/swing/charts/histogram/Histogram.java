package nl.tudelft.simulation.dsol.swing.charts.histogram;

import java.awt.Color;
import java.awt.Container;
import java.awt.GradientPaint;
import java.io.Serializable;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.event.EventProducerInterface;
import org.djutils.event.EventTypeInterface;
import org.djutils.event.ref.ReferenceType;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;

import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.SimCounter;
import nl.tudelft.simulation.dsol.swing.Swingable;
import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * The histogram specifies a histogram chart for the DSOL framework.
 * <p>
 * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology </a>, the Netherlands. <br>
 * See for project information <a href="https://simulation.tudelft.nl"> www.simulation.tudelft.nl </a>.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="mailto:a.verbraeck@tudelft.nl"> Alexander Verbraeck </a> <br>
 *         <a href="https://www.linkedin.com/in/peterhmjacobs"> Peter Jacobs </a>
 * @since 1.5
 */
public class Histogram implements Swingable, Serializable
{
    /** */
    private static final long serialVersionUID = 20200108L;

    /** LABEL_X_AXIS is the label on the X-axis. */
    public static final String LABEL_X_AXIS = "X";

    /** LABEL_Y_AXIS is the label on the Y-axis. */
    public static final String LABEL_Y_AXIS = "#";

    /** chart refers to the chart. */
    protected JFreeChart chart = null;

    /** dataset refers to the dataset. */
    protected HistogramDataset dataset = null;

    /**
     * constructs a new Histogram.
     * @param title String; the title
     * @param domain double[]; the domain
     * @param numberofBins int; the numberofbins
     */
    public Histogram(final String title, final double[] domain, final int numberofBins)
    {
        this(title, domain, null, numberofBins);
    }

    /**
     * constructs a new Histogram.
     * @param title String; the title. The title of the histogram
     * @param domain double[]; the domain of the x-axis.
     * @param range double[]; the y-axis range of the histogram.
     * @param numberofBins int; the numberofbins of this histogram.
     */
    public Histogram(final String title, final double[] domain, final double[] range, final int numberofBins)
    {
        super();
        this.dataset = new HistogramDataset(title, domain, range, numberofBins);

        this.chart = ChartFactory.createHistogram(title, LABEL_X_AXIS, LABEL_Y_AXIS, this.dataset, PlotOrientation.VERTICAL,
                true, true, true);
        this.chart.setBackgroundPaint(new GradientPaint(0.0F, 0.0F, Color.white, 1000F, 0.0F, Color.blue));

        this.chart.getXYPlot().setRangeAxis(new NumberAxis(Histogram.LABEL_Y_AXIS));
        this.chart.getXYPlot().getRangeAxis().setAutoRange(true);
        this.chart.getXYPlot()
                .setDomainAxis(new HistogramDomainAxis(this.chart.getXYPlot(), Histogram.LABEL_X_AXIS, domain, numberofBins));
        this.dataset.addChangeListener(this.chart.getXYPlot());
    }

    /**
     * constructs a new Histogram that is registered in the simulator-provided jndi context.
     * @param simulator SimulatorInterface&lt;?, ?, ?&gt;; the simulator
     * @param title String; the title of the plot
     * @param domain double[]; the domain of the plot
     * @param range double[]; the range of the plot
     * @param numberofBins int; the number of bins in this plot
     */
    public Histogram(final SimulatorInterface<?> simulator, final String title, final double[] domain,
            final double[] range, final int numberofBins)
    {
        this(title, domain, range, numberofBins);
        try
        {
            ContextInterface context = ContextUtil.lookupOrCreateSubContext(simulator.getReplication().getContext(), "charts");
            context.bindObject(this);
        }
        catch (NamingException | RemoteException exception)
        {
            simulator.getLogger().always().warn(exception, "<init>");
        }
    }

    /**
     * constructs a new Histogram that is registered in the simulator-provided jndi context.
     * @param simulator SimulatorInterface&lt;?, ?, ?&gt;; the simulator
     * @param title String; the title
     * @param domain double[]; the domain
     * @param numberofBins int; the number of bins
     */
    public Histogram(final SimulatorInterface<?> simulator, final String title, final double[] domain,
            final int numberofBins)
    {
        this(title, domain, numberofBins);
        try
        {
            ContextInterface context = ContextUtil.lookupOrCreateSubContext(simulator.getReplication().getContext(), "charts");
            context.bindObject(this);
        }
        catch (NamingException | RemoteException exception)
        {
            simulator.getLogger().always().warn(exception, "<init>");
        }
    }

    /**
     * adds a counter to the histogramdataset. This histogram then subscribes its dataset to the
     * <code>Counter.COUNT_EVENT</code>.
     * @param counter Counter; the counter to add.
     */
    public synchronized void add(final SimCounter<?> counter)
    {
        HistogramSeries set = this.getDataset().addSeries(counter.getDescription());
        counter.addListener(set, SimCounter.TIMED_OBSERVATION_ADDED_EVENT, ReferenceType.STRONG);
    }

    /**
     * adds an eventProducer to the histogram dataset. The histogram subscribes its dataset subsequentially to the specified
     * event.
     * @param description String; the description of the eventProducer
     * @param source EventProducerInterface; the eventproducer which functions as source for this histogram.
     * @param eventType EventType; the eventType.
     * @throws RemoteException on network error for the (possibly remote) event listener
     */
    public synchronized void add(final String description, final EventProducerInterface source,
            final EventTypeInterface eventType) throws RemoteException
    {
        HistogramSeries set = this.getDataset().addSeries(description);
        source.addListener(set, eventType, ReferenceType.STRONG);
    }

    /**
     * returns the chart.
     * @return JFreeChart
     */
    public JFreeChart getChart()
    {
        return this.chart;
    }

    /** {@inheritDoc} */
    @Override
    public Container getSwingPanel()
    {
        ChartPanel result = new ChartPanel(this.chart);
        result.setMouseZoomable(true, false);
        return result;
    }

    /**
     * returns the dataset of a histogram.
     * @return the HistogramDataset containing all series.
     */
    public HistogramDataset getDataset()
    {
        return this.dataset;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return getChart().getTitle().getText();
    }

}
