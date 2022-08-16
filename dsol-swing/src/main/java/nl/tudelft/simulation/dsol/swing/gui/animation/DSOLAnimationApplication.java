package nl.tudelft.simulation.dsol.swing.gui.animation;

import java.rmi.RemoteException;

import org.djutils.draw.bounds.Bounds2d;

import nl.tudelft.simulation.dsol.swing.gui.DSOLApplication;
import nl.tudelft.simulation.dsol.swing.gui.DSOLPanel;
import nl.tudelft.simulation.language.DSOLException;

/**
 * Wrap a DSOL simulation model, or any (descendant of a) JPanel in a JFrame (wrap it in a window). The window will be
 * maximized. Code based on OpenTrafficSim project component with the same purpose.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class DSOLAnimationApplication extends DSOLApplication
{
    /** */
    private static final long serialVersionUID = 20190118L;

    /** the animation panel in a tab. */
    private DSOLAnimationTab animationTab;

    /**
     * Create a DSOL application with animation. 
     * @param panel DSOLPanel; this should be the tabbed panel of the simulation
     * @param title String; the title of the window
     * @param homeExtent Bounds2d; the home extent of the animation
     * @throws RemoteException on network error
     * @throws DSOLException when simulator does not implement the AnimatorInterface
     */
    public DSOLAnimationApplication(final DSOLPanel panel, final String title, final Bounds2d homeExtent)
            throws RemoteException, DSOLException
    {
        super(panel, title);
        this.animationTab = new DSOLAnimationTab(homeExtent, panel.getSimulator());
        panel.addTab(0, "animation", this.animationTab);
        panel.getTabbedPane().setSelectedIndex(0); // select the animation pane as default (can be overridden)
        setAppearance(getAppearance()); // update appearance of added objects
    }

    /**
     * Create a DSOL application with a custom animation tab. 
     * @param panel DSOLPanel; this should be the tabbed panel of the simulation
     * @param title String; the title of the window
     * @param animationTab DSOLAnimationTab; the animation tab to add, e.g. one containing GIS
     * @throws RemoteException on network error
     * @throws DSOLException when simulator does not implement the AnimatorInterface
     */
    public DSOLAnimationApplication(final DSOLPanel panel, final String title, final DSOLAnimationTab animationTab)
            throws RemoteException, DSOLException
    {
        super(panel, title);
        this.animationTab = animationTab;
        panel.addTab(0, "animation", this.animationTab);
        panel.getTabbedPane().setSelectedIndex(0); // select the animation pane as default (can be overridden)
        setAppearance(getAppearance()); // update appearance of added objects
    }

    /**
     * @return DSOLAnimationTab; the animationTab
     */
    public DSOLAnimationTab getAnimationTab()
    {
        return this.animationTab;
    }

}
