package nl.tudelft.simulation.dsol.swing.charts.xy;

import java.awt.Color;
import java.awt.Container;
import java.awt.GradientPaint;
import java.io.Serializable;
import java.rmi.RemoteException;

import javax.naming.NamingException;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;

import org.djutils.event.EventProducer;
import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.event.reference.ReferenceType;
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
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
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
     * @param simulator the simulator
     * @param title the title
     * @param domain the domain
     * @param range the range
     * @param axisType the type of the axsis
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
     * @param simulator the simulator
     * @param title the title
     */
    public XYChart(final SimulatorInterface<?> simulator, final String title)
    {
        this(simulator, title, new double[] {0, simulator.getReplication().getRunLength().doubleValue()});
    }

    /**
     * constructs a new XYChart that is registered in the simulator-provided jndi context.
     * @param simulator the simulator
     * @param title the title
     * @param axisType the axisType to use.
     */
    public XYChart(final SimulatorInterface<?> simulator, final String title, final short axisType)
    {
        this(simulator, title, new double[] {0, simulator.getReplication().getRunLength().doubleValue()}, axisType);
    }

    /**
     * constructs a new XYChart that is registered in the simulator-provided jndi context.
     * @param simulator the simulator
     * @param title the title
     * @param domain the domain
     */
    public XYChart(final SimulatorInterface<?> simulator, final String title, final double[] domain)
    {
        this(simulator, title, domain, null, XYChart.XLINEAR_YLINEAR);
    }

    /**
     * constructs a new XYChart that is registered in the simulator-provided jndi context.
     * @param simulator the simulator
     * @param title the title
     * @param period the period
     */
    public XYChart(final SimulatorInterface<?> simulator, final String title, final double period)
    {
        this(simulator, title, new double[] {Double.NaN, period}, null, XYChart.XLINEAR_YLINEAR);
    }

    /**
     * constructs a new XYChart that is registered in the simulator-provided jndi context.
     * @param simulator the simulator
     * @param title the title
     * @param domain the domain
     * @param axisType the axisType to use.
     */
    public XYChart(final SimulatorInterface<?> simulator, final String title, final double[] domain, final short axisType)
    {
        this(simulator, title, domain, null, axisType);
    }

    /**
     * constructs a new XYChart that is registered in the simulator-provided jndi context.
     * @param simulator the simulator
     * @param title the title
     * @param period the period
     * @param axisType the axisType to use.
     */
    public XYChart(final SimulatorInterface<?> simulator, final String title, final double period, final short axisType)
    {
        this(simulator, title, new double[] {Double.NaN, period}, null, axisType);
    }

    /**
     * constructs a new XYChart that is registered in the simulator-provided jndi context.
     * @param simulator the simulator
     * @param title the title
     * @param domain the domain
     * @param range the range
     */
    public XYChart(final SimulatorInterface<?> simulator, final String title, final double[] domain, final double[] range)
    {
        this(simulator, title, domain, range, XYChart.XLINEAR_YLINEAR);
    }

    /**
     * constructs a new XYChart that is registered in the simulator-provided jndi context.
     * @param simulator the simulator
     * @param title the title
     * @param period the period
     * @param range the range
     */
    public XYChart(final SimulatorInterface<?> simulator, final String title, final double period, final double[] range)
    {
        this(simulator, title, new double[] {Double.NaN, period}, range, XYChart.XLINEAR_YLINEAR);
    }

    /**
     * adds a tally to the xyChart.
     * @param persistent the persistent
     * @throws RemoteException on network failure
     */
    public void add(final SimPersistent<?> persistent) throws RemoteException
    {
        XYSeries set = new XYSeries(persistent.getDescription(), this.simulator, this.axisType, this.period);
        persistent.addListener(set, SimPersistent.TIMED_OBSERVATION_ADDED_EVENT, ReferenceType.STRONG);
        this.getDataset().addSeries(set);
    }

    /**
     * adds an eventProducer to the xyChart.
     * @param description the description of the eventProducer
     * @param source the source
     * @param eventType the event
     * @throws RemoteException on network failure
     */
    public void add(final String description, final EventProducer source, final EventType eventType) throws RemoteException
    {
        XYSeries set = new XYSeries(description, this.simulator, this.axisType, this.period);
        source.addListener(set, eventType, LocalEventProducer.FIRST_POSITION, ReferenceType.STRONG);
        this.getDataset().addSeries(set);
    }

    /**
     * Set the label for the X-axis of the XY plot.
     * @param xLabel the new label for the X axis
     * @return the chart for method chaining
     */
    public XYChart setLabelXAxis(final String xLabel)
    {
        getChart().getXYPlot().getDomainAxis().setLabel(xLabel);
        return this;
    }

    /**
     * Set the label for the Y-axis of the XY plot.
     * @param yLabel the new label for the X axis
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
     * @param filter the filter to apply
     */
    public void setFilter(final FilterInterface filter)
    {
        this.dataset.setFilter(filter);
    }

    @Override
    public String toString()
    {
        return getChart().getTitle().getText();
    }

}
