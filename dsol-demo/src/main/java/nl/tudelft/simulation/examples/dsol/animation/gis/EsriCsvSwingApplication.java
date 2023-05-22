package nl.tudelft.simulation.examples.dsol.animation.gis;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.io.URLResource;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.animation.d2.RenderableScale;
import nl.tudelft.simulation.dsol.animation.gis.GisRenderable2d;
import nl.tudelft.simulation.dsol.animation.gis.esri.EsriFileCsvParser;
import nl.tudelft.simulation.dsol.animation.gis.esri.EsriRenderable2d;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsRealTimeAnimator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.swing.gui.DSOLPanel;
import nl.tudelft.simulation.dsol.swing.gui.animation.DSOLAnimationApplication;
import nl.tudelft.simulation.dsol.swing.gui.animation.DSOLAnimationGisTab;
import nl.tudelft.simulation.dsol.swing.gui.control.RealTimeControlPanel;
import nl.tudelft.simulation.language.DsolException;

/**
 * GIS demo to show a map using ESRI shape files.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class EsriCsvSwingApplication extends DSOLAnimationApplication
{
    /**
     * @param title String; the title
     * @param panel DSOLPanel; the panel
     * @param animationTab DSOLAnimationGisTab; the (custom) animation tab
     * @throws DsolException when simulator is not an animator
     * @throws IllegalArgumentException for illegal bounds
     * @throws RemoteException on network error
     */
    public EsriCsvSwingApplication(final String title, final DSOLPanel panel, final DSOLAnimationGisTab animationTab)
            throws RemoteException, IllegalArgumentException, DsolException
    {
        super(panel, title, animationTab);
        panel.enableSimulationControlButtons();
    }

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param args String[]; arguments, expected to be empty
     * @throws SimRuntimeException on error
     * @throws RemoteException on error
     * @throws NamingException on error
     * @throws DsolException when simulator is not an animator
     */
    public static void main(final String[] args) throws SimRuntimeException, RemoteException, NamingException, DsolException
    {
        DevsRealTimeAnimator.TimeDouble simulator = new DevsRealTimeAnimator.TimeDouble("EsriSwingApplication", 0.001);
        EmptyModel model = new EmptyModel(simulator);
        Replication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 1000000.0);
        simulator.initialize(model, replication);

        DSOLPanel panel = new DSOLPanel(new RealTimeControlPanel.TimeDouble(model, simulator));
        Bounds2d mapBounds = new Bounds2d(4.355, 4.386, 51.995, 52.005);
        DSOLAnimationGisTab animationTab = new DSOLAnimationGisTab(mapBounds, simulator);
        animationTab.getAnimationPanel().setRenderableScale(
                new RenderableScale(Math.cos(Math.toRadians(mapBounds.midPoint().getY())), 1.0 / 111319.24));
        animationTab.addAllToggleGISButtonText("MAP LAYERS", model.getGisMap(), "hide or show this GIS layer");
        new EsriCsvSwingApplication("EsriSwingApplication", panel, animationTab);
    }

    /** The empty model -- this demo is just to show a map on the screen. */
    static class EmptyModel extends AbstractDsolModel<Double, DevsSimulatorInterface<Double>>
    {
        /** The default serial version UID for serializable classes. */
        private static final long serialVersionUID = 1L;

        /** the GIS map. */
        private GisRenderable2d gisMap;

        /**
         * constructs a new EmptyModel.
         * @param simulator DevsSimulatorInterface&lt;Double&gt;; the simulator
         */
        EmptyModel(final DevsSimulatorInterface<Double> simulator)
        {
            super(simulator);
        }

        /** {@inheritDoc} */
        @Override
        public void constructModel() throws SimRuntimeException
        {
            URL csvUrl = URLResource.getResource("/resources/esri/tudelft.csv");
            System.out.println("ESRI-map file: " + csvUrl.toString());
            try
            {
                this.gisMap = new EsriRenderable2d(getSimulator().getReplication(),
                        EsriFileCsvParser.parseMapFile(csvUrl, "TU Delft"));
            }
            catch (IOException e)
            {
                throw new SimRuntimeException(e);
            }
        }

        /**
         * @return gisMap
         */
        public GisRenderable2d getGisMap()
        {
            return this.gisMap;
        }
    }
}
