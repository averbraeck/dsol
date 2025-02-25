package nl.tudelft.simulation.examples.dsol.animation;

import java.rmi.RemoteException;

import org.djutils.draw.bounds.Bounds2d;
import org.pmw.tinylog.Level;

import nl.tudelft.simulation.dsol.animation.d2.RenderableScale;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.simulators.DevsRealTimeAnimator;
import nl.tudelft.simulation.dsol.swing.gui.ConsoleLogger;
import nl.tudelft.simulation.dsol.swing.gui.ConsoleOutput;
import nl.tudelft.simulation.dsol.swing.gui.DsolPanel;
import nl.tudelft.simulation.dsol.swing.gui.animation.DsolAnimationApplication;
import nl.tudelft.simulation.dsol.swing.gui.animation.DsolAnimationTab;
import nl.tudelft.simulation.dsol.swing.gui.animation.panel.SearchPanel.ObjectKind;
import nl.tudelft.simulation.dsol.swing.gui.control.RealTimeControlPanel;
import nl.tudelft.simulation.language.DsolException;

/**
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class BallSwingApplication extends DsolAnimationApplication
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param title String; the title
     * @param panel DsolPanel; the panel
     * @throws DsolException when simulator is not an animator
     * @throws IllegalArgumentException for illegal bounds
     * @throws RemoteException on network error
     */
    public BallSwingApplication(final String title, final DsolPanel panel)
            throws RemoteException, IllegalArgumentException, DsolException
    {
        super(panel, title, DsolAnimationTab.createAutoPanTab(new Bounds2d(-100, 100, -100, 100), panel.getSimulator()));
        getAnimationTab().getAnimationPanel().setRenderableScale(new RenderableScale(2.0, 0.5));

        ObjectKind<Ball> objectKind = new ObjectKind<Ball>("Ball")
        {
            @Override
            public Ball searchObject(final String id)
            {
                if (id == null || id.length() == 0)
                {
                    return null;
                }
                try
                {
                    return ((BallModel) panel.getModel()).getBall(Integer.valueOf(id));
                }
                catch (Exception e)
                {
                    return null;
                }
            }

        };
        getAnimationTab().getSearchPanel().addObjectKind(objectKind);

        panel.enableSimulationControlButtons();
    }

    /**
     * @param args String[]; arguments, expected to be empty
     * @throws Exception on error
     */
    public static void main(final String[] args) throws Exception
    {
        DevsRealTimeAnimator.TimeDouble simulator = new DevsRealTimeAnimator.TimeDouble("BallSwingApplication", 0.001);
        BallModel model = new BallModel(simulator);
        Replication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 1000000.0);
        simulator.initialize(model, replication);
        DsolPanel panel = new DsolPanel(new RealTimeControlPanel.TimeDouble(model, simulator));
        panel.addTab("logger", new ConsoleLogger(Level.INFO));
        panel.addTab("console", new ConsoleOutput());
        new BallSwingApplication("BallSwingApplication", panel);
    }
}
