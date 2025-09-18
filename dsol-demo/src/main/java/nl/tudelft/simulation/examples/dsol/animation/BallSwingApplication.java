package nl.tudelft.simulation.examples.dsol.animation;

import org.djutils.draw.bounds.Bounds2d;

import nl.tudelft.simulation.dsol.animation.d2.RenderableScale;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.simulators.DevsRealTimeAnimator;
import nl.tudelft.simulation.dsol.swing.gui.ConsoleOutput;
import nl.tudelft.simulation.dsol.swing.gui.DsolPanel;
import nl.tudelft.simulation.dsol.swing.gui.LoggerConsole;
import nl.tudelft.simulation.dsol.swing.gui.animation.DsolAnimationApplication;
import nl.tudelft.simulation.dsol.swing.gui.animation.DsolAnimationTab;
import nl.tudelft.simulation.dsol.swing.gui.animation.panel.SearchPanel.ObjectKind;
import nl.tudelft.simulation.dsol.swing.gui.control.RealTimeControlPanel;
import nl.tudelft.simulation.language.DsolRuntimeException;

/**
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class BallSwingApplication
{
    /**
     * Build the demo ball animation application with reset option.
     */
    public void build()
    {
        try
        {
            DevsRealTimeAnimator.TimeDouble simulator = new DevsRealTimeAnimator.TimeDouble("sim", 0.001);
            BallModel model = new BallModel(simulator);
            Replication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 1000000.0);
            simulator.initialize(model, replication);
            DsolPanel panel = new DsolPanel(new RealTimeControlPanel.TimeDouble(model, simulator));
            panel.addTab("logger", new LoggerConsole());
            panel.addTab("console", new ConsoleOutput());
            var frame = new DsolAnimationApplication(panel, "BallSwingApplication",
                    DsolAnimationTab.createAutoPanTab(new Bounds2d(-120, 120, -120, 120), panel.getSimulator()));

            frame.getAnimationTab().getAnimationPanel().setRenderableScale(new RenderableScale(2.0, 0.5));

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
            frame.getAnimationTab().getSearchPanel().addObjectKind(objectKind);
            panel.enableSimulationControlButtons();
            model.setResetApplicationExecutable(() -> build());
        }
        catch (Exception e)
        {
            throw new DsolRuntimeException(e);
        }
    }

    /**
     * @param args arguments, expected to be empty
     */
    public static void main(final String[] args)
    {
        var app = new BallSwingApplication();
        app.build();
    }
}
