package nl.tudelft.simulation.dsol.swing.charts.histogram;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleEdge;

/**
 * The histogram domainAxis defines the x-Axis of a histogram.
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
public class HistogramDomainAxis extends NumberAxis
{
    /** */
    private static final long serialVersionUID = 1L;

    /** labels refers to the labels to be printed. */
    protected String[] labels = null;

    /** maxLabelHeight refers to the maximum label heigth. */
    protected double maxLabelHeight = -1;

    /**
     * constructs a new HistogramDomainAxis.
     * @param parent XYPlot; the plot to which this axis belongs
     * @param label String; the label of the axis
     * @param domain double[]; the domain
     * @param numberOfBins int; the numberOfBins
     */
    public HistogramDomainAxis(final XYPlot parent, final String label, final double[] domain, final int numberOfBins)
    {
        super(label);
        this.setAutoRange(false);
        double binWidth = (domain[1] - domain[0]) / numberOfBins * 1.0;
        this.setLowerBound(domain[0] - binWidth);
        this.setUpperBound(domain[1] + binWidth);
        this.setVerticalTickLabels(true);
        // Let's copy the font etc from the y-axis
        this.setLabelFont(parent.getRangeAxis().getLabelFont());
        this.setTickLabelFont(parent.getRangeAxis().getTickLabelFont());
        this.labels = this.createLabels(domain, numberOfBins);
    }

    /** {@inheritDoc} */
    @Override
    public double valueToJava2D(final double value, final Rectangle2D dataArea, final RectangleEdge edge)
    {
        double ratio = (value - this.getLowerBound()) / (this.getUpperBound() - this.getLowerBound());
        return dataArea.getX() + ratio * (dataArea.getWidth());
    }

    /** {@inheritDoc} */
    @Override
    public double java2DToValue(final double value, final Rectangle2D dataArea, final RectangleEdge edge)
    {
        double ratio = (value - dataArea.getX()) / dataArea.getWidth();
        return this.getLowerBound() + ratio * (this.getUpperBound() - this.getLowerBound());
    }

    /** {@inheritDoc} */
    @Override
    public AxisSpace reserveSpace(final Graphics2D g2, final Plot dataPlot, final Rectangle2D dataArea,
            final RectangleEdge edge, final AxisSpace axisSpace)
    {
        if (this.maxLabelHeight == -1)
        {
            g2.setFont(this.getTickLabelFont());
            // If we have not yet measured the heigth of the labels
            for (int i = 0; i < this.labels.length; i++)
            {
                // we draw 90degrees rotated, so heigth = width
                double height = g2.getFont().getStringBounds(this.labels[i], g2.getFontRenderContext()).getWidth();
                if (height > this.maxLabelHeight)
                {
                    this.maxLabelHeight = height + 3;
                }
            }
        }
        AxisSpace result = new AxisSpace();
        result.add(this.maxLabelHeight, RectangleEdge.BOTTOM);
        return result;
    }

    /**
     * creates the labels for the axis
     * @param domain double[]; the domain of the histogram
     * @param numberOfBins int; the number of bins
     * @return the result
     */
    private String[] createLabels(final double[] domain, final int numberOfBins)
    {
        String[] result = new String[numberOfBins + 2];
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(2);
        double binWidth = (domain[1] - domain[0]) / numberOfBins * 1.0;
        double start = domain[0];
        for (int i = 1; i < numberOfBins + 1; i++)
        {
            result[i] = formatter.format(start);
            start = start + binWidth;
        }
        // Let's write infinity and -infinity
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        result[0] = "-" + symbols.getInfinity();
        result[numberOfBins + 1] = symbols.getInfinity();
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public AxisState draw(final Graphics2D g2, final double cursor, final Rectangle2D plotArea, final Rectangle2D dataArea,
            final RectangleEdge edge, final PlotRenderingInfo arg5)
    {
        g2.setColor(Color.BLACK);
        g2.setFont(this.getTickLabelFont());
        double labelWidth = g2.getFont().getStringBounds(this.labels[0], g2.getFontRenderContext()).getHeight();
        double width = dataArea.getWidth() / (this.labels.length) * 1.0;
        double x = dataArea.getX() + 0.5 * width;
        double y = dataArea.getY() + dataArea.getHeight();
        g2.translate(x, y);
        g2.rotate(-Math.PI / 2.0);
        double offset = 0.0;
        for (int i = 0; i < this.labels.length; i++)
        {
            double labelHeight = g2.getFont().getStringBounds(this.labels[i], g2.getFontRenderContext()).getWidth() + 3;
            g2.drawString(this.labels[i], Math.round(-labelHeight), Math.round(offset + 0.33 * labelWidth));
            offset = offset + width;
        }
        g2.rotate(Math.PI / 2.0);
        g2.translate(-x, -y);
        return new AxisState();
    }
}
