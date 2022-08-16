package nl.tudelft.simulation.dsol.swing.charts.xy;

import java.awt.Color;
import java.awt.Container;
import java.awt.GradientPaint;
import java.io.Serializable;
import java.rmi.RemoteException;

import javax.naming.NamingException;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;

import org.djutils.event.EventProducerInterface;
import org.djutils.event.EventTypeInterface;
import org.djutils.event.ref.ReferenceType;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.plot.PlotOrientation;

import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.SimPersistent;
import nl.tudelft.simulation.dsol.swing.Swingable;
import nl.tudelft.simulation.language.filters.FilterInterface;
import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * The xyChart specifies the xyChart in DSOL.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class XYChart implements Swingable, Serializable
{
    /** */
    private static final long serialVersionUID = 20200108L;

    /** x-axis is linear y-axis is linear. */
    public static final short XLINEAR_YLINEAR = 0;

    /** x-axis is linear y-axis is logarithmic. */
    public static final short XLINEAR_YLOGARITHMIC = 1;

    /** x-axis is logarithmic y-axis is linear. */
    public static final short XLOGARITHMIC_YLINEAR = 2;

    /** x-axis is logarithmic y-axis is logarithmic. */
    public static final short XLOGARITHMIC_YLOGARITHMIC = 3;

    /** LABEL_X_AXIS is the label on the X-axis. */
    private static final String LABEL_X_AXIS = "X";

    /** LABEL_Y_AXIS is the label on the Y-axis. */
    private static final String LABEL_Y_AXIS = "value";

    /** chart refers to the chart. */
    protected JFreeChart chart = null;

    /** the simulator. */
    final SimulatorInterface<?> simulator;

    /** dataset refers to the dataset. */
    protected XYDataset dataset = new XYDataset();

    /** the axis type of the chart. */
    protected short axisType = XLINEAR_YLINEAR;

    /** the period to show on the domain axis. */
    private double period = Double.POSITIVE_INFINITY;

    /**
     * constructs a new XYChart and bind it to the replication context.
     * @param simulator SimulatorInterface; the simulator
     * @param title String; the title
     * @param domain double[]; the domain
     * @param range double[]; the range
     * @param axisType short; the type of the axsis
     */
    public XYChart(final SimulatorInterface<?> simulator, final String title, final double[] domain, final double[] range,
            final short axisType)
    {
        this.simulator = simulator;
        this.chart = ChartFactory.createXYLineChart(title, LABEL_X_AXIS, LABEL_Y_AXIS, this.dataset, PlotOrientation.VERTICAL,
                true, true, true);
        this.chart.setBackgroundPaint(new GradientPaint(0.0F, 0.0F, Color.white, 1000F, 0.0F, Color.blue));
        this.axisType = axisType;
        switch (this.axisType)
        {
            case XYChart.XLINEAR_YLOGARITHMIC:
                this.chart.getXYPlot().setRangeAxis(new LogarithmicAxis(XYChart.LABEL_Y_AXIS));
                break;
            case XYChart.XLOGARITHMIC_YLINEAR:
                this.chart.getXYPlot().setDomainAxis(new LogarithmicAxis(XYChart.LABEL_X_AXIS));
                break;
            case XYChart.XLOGARITHMIC_YLOGARITHMIC:
                this.chart.getXYPlot().setDomainAxis(new LogarithmicAxis(XYChart.LABEL_X_AXIS));
                this.chart.getXYPlot().setRangeAxis(new LogarithmicAxis(XYChart.LABEL_Y_AXIS));
                break;
            default:
                break;
        }

        if (domain != null)
        {
            if (Double.isNaN(domain[0]))
            {
                this.chart.getXYPlot().getDomainAxis().setAutoRange(true);
                this.period = domain[1];
            }
            else
            {
                this.chart.getXYPlot().getDomainAxis().setAutoRange(false);
                this.chart.getXYPlot().getDomainAxis().setLowerBound(domain[0]);
                this.chart.getXYPlot().getDomainAxis().setUpperBound(domain[1]);
            }
        }
        else
        {
            this.chart.getXYPlot().getDomainAxis().setAutoRange(true);
        }
        if (range != null)
        {
            this.chart.getXYPlot().getRangeAxis().setAutoRange(false);
            this.chart.getXYPlot().getRangeAxis().setLowerBound(range[0]);
            this.chart.getXYPlot().getRangeAxis().setUpperBound(range[1]);
        }
        else
        {
            this.chart.getXYPlot().getRangeAxis().setAutoRange(true);
        }
        this.dataset.addChangeListener(this.chart.getXYPlot());
        this.getChart().fireChartChanged();

        // bind to context
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
     * constructs a new XYChart that is registered in the simulator-provided jndi context.
     * @param simulator SimulatorInterface&lt;?, ?, ?&gt;; the simulator
     * @param title String; the title
     */
    public XYChart(final SimulatorInterface<?> simulator, final String title)
    {
        this(simulator, title, new double[] {0, simulator.getReplication().getRunLength().doubleValue()});
    }

    /**
     * constructs a new XYChart that is registered in the simulator-provided jndi context.
     * @param simulator SimulatorInterface&lt;?, ?, ?&gt;; the simulator
     * @param title String; the title
     * @param axisType short; the axisType to use.
     */
    public XYChart(final SimulatorInterface<?> simulator, final String title, final short axisType)
    {
        this(simulator, title, new double[] {0, simulator.getReplication().getRunLength().doubleValue()}, axisType);
    }

    /**
     * constructs a new XYChart that is registered in the simulator-provided jndi context.
     * @param simulator SimulatorInterface&lt;?, ?, ?&gt;; the simulator
     * @param title String; the title
     * @param domain double[]; the domain
     */
    public XYChart(final SimulatorInterface<?> simulator, final String title, final double[] domain)
    {
        this(simulator, title, domain, null, XYChart.XLINEAR_YLINEAR);
    }

    /**
     * constructs a new XYChart that is registered in the simulator-provided jndi context.
     * @param simulator SimulatorInterface&lt;?, ?, ?&gt;; the simulator
     * @param title String; the title
     * @param period double; the period
     */
    public XYChart(final SimulatorInterface<?> simulator, final String title, final double period)
    {
        this(simulator, title, new double[] {Double.NaN, period}, null, XYChart.XLINEAR_YLINEAR);
    }

    /**
     * constructs a new XYChart that is registered in the simulator-provided jndi context.
     * @param simulator SimulatorInterface&lt;?, ?, ?&gt;; the simulator
     * @param title String; the title
     * @param domain double[]; the domain
     * @param axisType short; the axisType to use.
     */
    public XYChart(final SimulatorInterface<?> simulator, final String title, final double[] domain, final short axisType)
    {
        this(simulator, title, domain, null, axisType);
    }

    /**
     * constructs a new XYChart that is registered in the simulator-provided jndi context.
     * @param simulator SimulatorInterface&lt;?, ?, ?&gt;; the simulator
     * @param title String; the title
     * @param period double; the period
     * @param axisType short; the axisType to use.
     */
    public XYChart(final SimulatorInterface<?> simulator, final String title, final double period, final short axisType)
    {
        this(simulator, title, new double[] {Double.NaN, period}, null, axisType);
    }

    /**
     * constructs a new XYChart that is registered in the simulator-provided jndi context.
     * @param simulator SimulatorInterface&lt;?, ?, ?&gt;; the simulator
     * @param title String; the title
     * @param domain double[]; the domain
     * @param range double[]; the range
     */
    public XYChart(final SimulatorInterface<?> simulator, final String title, final double[] domain, final double[] range)
    {
        this(simulator, title, domain, range, XYChart.XLINEAR_YLINEAR);
    }

    /**
     * constructs a new XYChart that is registered in the simulator-provided jndi context.
     * @param simulator SimulatorInterface&lt;?, ?, ?&gt;; the simulator
     * @param title String; the title
     * @param period double; the period
     * @param range double[]; the range
     */
    public XYChart(final SimulatorInterface<?> simulator, final String title, final double period, final double[] range)
    {
        this(simulator, title, new double[] {Double.NaN, period}, range, XYChart.XLINEAR_YLINEAR);
    }

    /**
     * adds a tally to the xyChart.
     * @param persistent Persistent; the persistent
     */
    public void add(final SimPersistent<?> persistent)
    {
        XYSeries set = new XYSeries(persistent.getDescription(), this.simulator, this.axisType, this.period);
        persistent.addListener(set, SimPersistent.TIMED_OBSERVATION_ADDED_EVENT, ReferenceType.STRONG);
        this.getDataset().addSeries(set);
    }

    /**
     * adds an eventProducer to the xyChart.
     * @param description String; the description of the eventProducer
     * @param source EventProducerInterface; the source
     * @param eventType EventType; the event
     * @throws RemoteException on network failure
     */
    public void add(final String description, final EventProducerInterface source, final EventTypeInterface eventType)
            throws RemoteException
    {
        XYSeries set = new XYSeries(description, this.simulator, this.axisType, this.period);
        source.addListener(set, eventType, EventProducerInterface.FIRST_POSITION, ReferenceType.STRONG);
        this.getDataset().addSeries(set);
    }

    /**
     * Set the label for the X-axis of the XY plot.
     * @param xLabel String; the new label for the X axis
     * @return the chart for method chaining
     */
    public XYChart setLabelXAxis(final String xLabel)
    {
        getChart().getXYPlot().getDomainAxis().setLabel(xLabel);
        return this;
    }

    /**
     * Set the label for the Y-axis of the XY plot.
     * @param yLabel String; the new label for the X axis
     * @return the chart for method chaining
     */
    public XYChart setLabelYAxis(final String yLabel)
    {
        getChart().getXYPlot().getRangeAxis().setLabel(yLabel);
        return this;
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
        result.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        result.setMouseZoomable(true, false);
        return result;
    }

    /**
     * returns the dataset of a xyChart.
     * @return HistogramSeries
     */
    public XYDataset getDataset()
    {
        return this.dataset;
    }

    /**
     * applies a filter on the chart.
     * @param filter FilterInterface; the filter to apply
     */
    public void setFilter(final FilterInterface filter)
    {
        this.dataset.setFilter(filter);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return getChart().getTitle().getText();
    }

}
