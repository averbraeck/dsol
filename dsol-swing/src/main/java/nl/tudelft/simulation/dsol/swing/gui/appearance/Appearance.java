package nl.tudelft.simulation.dsol.swing.gui.appearance;

import java.awt.Color;

/**
 * Enum that contains a background color, foreground color and a font name, to be set throughout all components. Code based on
 * OpenTrafficSim project component with the same purpose.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="http://www.transport.citg.tudelft.nl">Wouter Schakel</a>
 */
public enum Appearance
{
    /** Dark. */
    DARK("Dark", new Color(96, 96, 96), Color.WHITE, Color.DARK_GRAY, "Verdana"),

    /** Gray. */
    GRAY("Gray", Color.LIGHT_GRAY, Color.BLACK, new Color(96, 96, 96), "Verdana"),

    /** Bright. */
    BRIGHT("Bright", Color.LIGHT_GRAY, Color.BLACK, Color.WHITE, "Verdana"),

    /** Light. */
    LIGHT("Light", Color.WHITE, Color.BLACK, Color.WHITE, "Verdana"),

    /** Legacy, as the initial DSOL applications had. */
    LEGACY("Legacy", new Color(238, 238, 238), Color.BLACK, Color.WHITE, "Dialog"),

    /** Red. */
    RED("Red", new Color(208, 192, 192), Color.RED.darker().darker(), new Color(208, 192, 192).darker(), "Verdana"),

    /** Green. */
    GREEN("Green", new Color(192, 208, 192), Color.GREEN.darker().darker(), new Color(192, 208, 192).darker(), "Verdana"),

    /** Blue. */
    BLUE("Blue", new Color(192, 192, 208), Color.BLUE.darker().darker(), new Color(192, 192, 208).darker(), "Verdana");

    /** Name. */
    private final String name;

    /** Background color. */
    private final Color background;

    /** Foreground color. */
    private final Color foreground;

    /** Backdrop color (animation panel). */
    private final Color backdrop;

    /** font name. */
    private final String font;

    /**
     * Constructor for Appearance.
     * @param name String; name
     * @param background Color; background color
     * @param foreground Color; foreground color
     * @param backdrop Color; backdrop color (animation panel)
     * @param font String; font name
     */
    Appearance(final String name, final Color background, final Color foreground, final Color backdrop, final String font)
    {
        this.name = name;
        this.background = background;
        this.foreground = foreground;
        this.backdrop = backdrop;
        this.font = font;
    }

    /**
     * Returns the name.
     * @return String; name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns the background color.
     * @return Color; color
     */
    public Color getBackground()
    {
        return this.background;
    }

    /**
     * Returns the foreground color.
     * @return Color; color
     */
    public Color getForeground()
    {
        return this.foreground;
    }

    /**
     * Returns the backdrop color.
     * @return Color; color
     */
    public Color getBackdrop()
    {
        return this.backdrop;
    }

    /**
     * Returns the font name.
     * @return String; font name
     */
    public String getFont()
    {
        return this.font;
    }

}
