package nl.tudelft.simulation.dsol.swing.gui.util;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.djutils.logger.CategoryLogger;

/**
 * Icons contains static methods to load a icon from disk.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public final class Icons
{
    /** Utility class. */
    private Icons()
    {
        // Utility class.
    }

    /**
     * Attempt to load and return an icon.
     * @param iconPath the path that is used to load the icon
     * @return or null if loading failed
     */
    public static final Icon loadIcon(final String iconPath)
    {
        try
        {
            return new ImageIcon(ImageIO.read(Resource.getResourceAsStream(iconPath)));
        }
        catch (NullPointerException | IOException npe)
        {
            CategoryLogger.always().error("Could not load icon from path " + iconPath);
            return null;
        }
    }

    /**
     * Attempt to load and return an icon, which will be made gray-scale.
     * @param iconPath the path that is used to load the icon
     * @return or null if loading failed
     */
    public static final Icon loadGrayscaleIcon(final String iconPath)
    {
        try
        {
            return new ImageIcon(GrayFilter.createDisabledImage(ImageIO.read(Resource.getResourceAsStream(iconPath))));
        }
        catch (NullPointerException | IOException e)
        {
            CategoryLogger.always().error("Could not load icon from path " + iconPath);
            return null;
        }
    }

}
