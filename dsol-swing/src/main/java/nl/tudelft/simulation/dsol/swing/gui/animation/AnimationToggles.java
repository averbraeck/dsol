package nl.tudelft.simulation.dsol.swing.gui.animation;

import nl.tudelft.simulation.dsol.animation.Locatable;

/**
 * Helper class to set the animation toggles for the animation panel. Code based on OpenTrafficSim project component with the
 * same purpose.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @author <a href="http://www.transport.citg.tudelft.nl">Wouter Schakel</a>
 */
public final class AnimationToggles
{
    /**
     * Do not instantiate this class.
     */
    private AnimationToggles()
    {
        // static class.
    }

    /**
     * Set a class to be shown in the animation to true.
     * @param panel the DsolAnimationPanel where the animation of a class has to be switched off
     * @param locatableClass the class for which the animation has to be shown.
     */
    public static void showAnimationClass(final DsolAnimationTab panel, final Class<? extends Locatable> locatableClass)
    {
        panel.getAnimationPanel().showClass(locatableClass);
        panel.updateAnimationClassCheckBox(locatableClass);
    }

    /**
     * Set a class to be shown in the animation to false.
     * @param panel the DsolAnimationPanel where the animation of a class has to be switched off
     * @param locatableClass the class for which the animation has to be shown.
     */
    public static void hideAnimationClass(final DsolAnimationTab panel, final Class<? extends Locatable> locatableClass)
    {
        panel.getAnimationPanel().hideClass(locatableClass);
        panel.updateAnimationClassCheckBox(locatableClass);
    }

}
