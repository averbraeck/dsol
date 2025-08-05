package nl.tudelft.simulation.dsol.swing.gui.control;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.simulators.RealTime;

/**
 * JPanel that contains a JSlider for setting the speed of the simulation using a provided scale. Helper methods are available
 * to create a logarithmic scale.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class RunSpeedSliderPanel extends JPanel
{
    /** */
    private static final long serialVersionUID = 20150408L;

    /** The JSlider that the user sees. */
    protected JSlider slider;

    /** the tick values, each representing a speed. */
    private final List<Double> tickValues = new ArrayList<>();

    /** the tick labels to display on the screen. */
    private final List<String> tickLabels = new ArrayList<>();

    /**
     * Construct a new RunSpeedSliderPanel with tickValues and tickLabels.
     * @param tickValues the tick values that indicate the speed factors for the simulator
     * @param tickLabels the tick labels to display on the screen
     * @param simulator the real-time simulator
     * @param initialValue the initial value for the speed factor
     * @param <T> the time type
     * @param <S> the simulator type
     */
    public <T extends Number & Comparable<T>, S extends DevsSimulatorInterface<T> & RealTime<T>> RunSpeedSliderPanel(
            final List<Double> tickValues, final List<String> tickLabels, final S simulator, final double initialValue)
    {
        Throw.whenNull(tickValues, "tickValues");
        Throw.whenNull(tickLabels, "tickLabels");
        Throw.whenNull(simulator, "simulator");
        Throw.when(tickLabels.size() != tickValues.size(), IllegalArgumentException.class,
                "tickLabels size <> tickValues size");
        Throw.when(tickLabels.size() == 0, IllegalArgumentException.class, "tickLabels size = 0");
        for (int i = 1; i < this.tickValues.size(); i++)
        {
            if (this.tickValues.get(i - 1) > this.tickValues.get(i))
                throw new IllegalArgumentException("tick values not in ascending order");
        }
        this.tickValues.addAll(tickValues);
        this.tickLabels.addAll(tickLabels);
        makeSlider(simulator, initialValue);
    }

    /**
     * Construct a new RunSpeedSliderPanel with tickValues and tickLabels.
     * @param tickMap the tick values that indicate the speed factors for the simulator, mapped to the tick labels
     * @param simulator the real-time simulator
     * @param initialValue the initial value for the speed factor
     * @param <T> the time type
     * @param <S> the simulator type
     */
    public <T extends Number & Comparable<T>, S extends DevsSimulatorInterface<T> & RealTime<T>> RunSpeedSliderPanel(
            final SortedMap<Double, String> tickMap, final S simulator, final double initialValue)
    {
        Throw.whenNull(tickMap, "tickMap");
        Throw.whenNull(simulator, "simulator");
        Throw.when(tickMap.size() == 0, IllegalArgumentException.class, "tickMap size = 0");

        for (var entry : tickMap.entrySet())
        {
            this.tickValues.add(entry.getKey());
            this.tickLabels.add(entry.getValue());
        }

        makeSlider(simulator, initialValue);
    }

    /**
     * Construct the actual slider and change listener. Method can be overridden.
     * @param simulator the real-time simulator
     * @param initialValue the initial value for the speed factor
     * @param <T> the time type
     * @param <S> the simulator type
     */
    public <T extends Number & Comparable<T>,
            S extends DevsSimulatorInterface<T> & RealTime<T>> void makeSlider(final S simulator, final double initialValue)
    {
        this.slider = new JSlider(SwingConstants.HORIZONTAL, 0, this.tickLabels.size() - 1, findTick(initialValue));
        this.slider.setPreferredSize(new Dimension(350, 45));
        var labelTable = new Hashtable<Integer, JLabel>();
        for (int i = 0; i < this.tickLabels.size(); i++)
            labelTable.put(i, new JLabel(this.tickLabels.get(i)));
        this.slider.setLabelTable(labelTable);
        this.slider.setPaintLabels(true);
        this.slider.setPaintTicks(true);
        this.slider.setMajorTickSpacing(1);
        this.add(this.slider);

        // initial value of simulation speed
        simulator.setSpeedFactor(this.tickValues.get(findTick(initialValue)));

        // adjust the simulation speed
        this.slider.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(final ChangeEvent ce)
            {
                JSlider source = (JSlider) ce.getSource();
                if (!source.getValueIsAdjusting())
                {
                    simulator.setSpeedFactor(RunSpeedSliderPanel.this.tickValues.get(source.getValue()));
                }
            }
        });
    }

    /**
     * Return the closest tick larger than or equal to the given speed factor.
     * @param speed the speed factor to search for
     * @return the closest tick larger than or equal to the given speed factor
     */
    private int findTick(final double speed)
    {
        for (int i = 0; i < this.tickValues.size(); i++)
        {
            if (speed <= this.tickValues.get(i))
                return i;
        }
        return this.tickValues.size() - 1;
    }

    /**
     * Helper method to make a logarithmic scale based on a minimum and maximum value, and a number of ticks per decade.
     * @param minimum the minimum value on the scale (the displayed scale may extend a little further than this value)
     * @param maximum the maximum value on the scale (the displayed scale may extend a little further than this value)
     * @param ticksPerDecade the number of steps per decade
     * @return a sorted map with tick values that indicate the speed factors for the simulator, mapped to tick labels
     */
    public static SortedMap<Double, String> makeLogScale(final double minimum, final double maximum, final int ticksPerDecade)
    {
        Throw.when(minimum <= 0 || maximum <= minimum, IllegalArgumentException.class,
                "Bad minimum or maximum: 0 < minimum < maximum)");

        int[] ratios = switch (ticksPerDecade)
        {
            case 1 -> new int[] {1};
            case 2 -> new int[] {1, 3};
            case 3 -> new int[] {1, 2, 5};
            default -> throw new IllegalArgumentException("Bad ticksPerDecade value (must be 1, 2 or 3)");
        };

        SortedMap<Double, String> tickMap = new TreeMap<>();
        int minPower = (int) Math.floor(Math.log10(minimum));
        int maxPower = (int) Math.ceil(Math.log10(maximum));
        for (int power = minPower; power <= maxPower; power++)
        {
            for (int ratio : ratios)
            {
                double speed = Math.pow(10, power) * ratio;
                String label = String.valueOf(speed);
                if (power < maxPower || ratio == 1) // only take 10^maxPower * 1
                {
                    if (label.endsWith(".0"))
                        label = label.substring(0, label.length() - 2);
                    if (label.endsWith("000000"))
                        label = label.substring(0, label.length() - 6) + "M";
                    else if (label.endsWith("000"))
                        label = label.substring(0, label.length() - 3) + "k";
                    tickMap.put(speed, label);
                }
            }
        }
        tickMap.put(1.0E12, "\u221E");
        return tickMap;
    }

    @Override
    public String toString()
    {
        return "RunSpeedSliderPanel";
    }

    /**
     * Set the speed factor on the slider to the best possible approximation of a given value.
     * @param factor the requested speed factor
     */
    public void setSpeedFactor(final double factor)
    {
        int tick = findTick(factor);
        if (this.slider.getValue() != tick)
        {
            this.slider.setValue(tick);
        }
    }
}
