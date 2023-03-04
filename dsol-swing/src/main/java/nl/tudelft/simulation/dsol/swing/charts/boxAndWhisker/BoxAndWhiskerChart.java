package nl.tudelft.simulation.dsol.swing.charts.boxAndWhisker;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.io.Serializable;
import java.rmi.RemoteException;

import javax.naming.NamingException;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;

import org.djutils.stats.summarizers.event.EventBasedTally;
import org.djutils.stats.summarizers.event.EventBasedTimestampWeightedTally;
import org.djutils.stats.summarizers.event.EventBasedWeightedTally;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;

import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.swing.Swingable;
import nl.tudelft.simulation.naming.context.ContextInterface;
import nl.tudelft.simulation.naming.context.util.ContextUtil;

/**
 * The BoxAndWhiskerChart specifies a Box-and-Whisker chart.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="mailto:a.verbraeck@tudelft.nl"> Alexander Verbraeck </a>
 */
public class BoxAndWhiskerChart implements Swingable, Serializable
{
    /** */
    private static final long serialVersionUID = 20200108L;

    /** TITLE_FONT refers to the font to be used for the title of the plot. */
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);

    /** chart refers to the actual chart. */
    private JFreeChart chart = null;

    /**
     * constructs a new BoxAndWhiskerChart.
     * @param title String; the title of the chart
     */
    public BoxAndWhiskerChart(final String title)
    {
        Plot plot = new BoxAndWhiskerPlot();
        this.chart = new JFreeChart(title, TITLE_FONT, plot, true);
        this.chart.setBackgroundPaint(new GradientPaint(0.0F, 0.0F, Color.white, 1000F, 0.0F, Color.blue));
    }

    /**
     * constructs a new BoxAndWhiskerChart, linked to the simulator-provided context.
     * @param simulator SimulatorInterface&lt;?, ?, ?&gt;; the simulator
     * @param title String; the title
     */
    public BoxAndWhiskerChart(final SimulatorInterface<?> simulator, final String title)
    {
        this(title);
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
     * adds a tally to the chart.
     * @param tally EventBasedTally; the tally to be added
     * @throws RemoteException on network failure
     */
    public void add(final EventBasedTally tally) throws RemoteException
    {
        ((BoxAndWhiskerPlot) this.chart.getPlot()).add(tally);
    }

    /**
     * adds a weighted tally to the chart.
     * @param tally EventBasedWeightedTally; the tally to be added
     * @throws RemoteException on network failure
     */
    public void add(final EventBasedWeightedTally tally) throws RemoteException
    {
        ((BoxAndWhiskerPlot) this.chart.getPlot()).add(tally);
    }

    /**
     * adds a weighted tally to the chart.
     * @param tally EventBasedTimestampWeightedTally; the tally to be added
     * @throws RemoteException on network failure
     */
    public void add(final EventBasedTimestampWeightedTally tally) throws RemoteException
    {
        ((BoxAndWhiskerPlot) this.chart.getPlot()).add(tally);
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
        result.setPreferredSize(new Dimension(800, 600));
        return result;
    }

    /**
     * Returns the confidence interval of the BoxAndWhiskerPlot.
     * @return the confidence interval of the BoxAndWhiskerPlot
     */
    public double getConfidenceInterval()
    {
        return ((BoxAndWhiskerPlot) this.chart.getPlot()).getConfidenceInterval();
    }

    /**
     * sets the confidence interval of the plot. The default value = 0.05 (=5%)
     * @param confidenceInterval double; the confidence interval
     */
    public void setConfidenceInterval(final double confidenceInterval)
    {
        ((BoxAndWhiskerPlot) this.chart.getPlot()).setConfidenceInterval(confidenceInterval);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return getChart().getTitle().getText();
    }

}
