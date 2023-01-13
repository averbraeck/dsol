package nl.tudelft.simulation.examples.dsol.animation.gis;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.io.URLResource;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.animation.D2.RenderableScale;
import nl.tudelft.simulation.dsol.animation.gis.GisRenderable2D;
import nl.tudelft.simulation.dsol.animation.gis.osm.OsmFileCsvParser;
import nl.tudelft.simulation.dsol.animation.gis.osm.OsmRenderable2D;
import nl.tudelft.simulation.dsol.experiment.ReplicationInterface;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;
import nl.tudelft.simulation.dsol.simulators.DevsxRealTimeAnimator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.swing.gui.DSOLPanel;
import nl.tudelft.simulation.dsol.swing.gui.animation.DSOLAnimationApplication;
import nl.tudelft.simulation.dsol.swing.gui.animation.DSOLAnimationGisTab;
import nl.tudelft.simulation.dsol.swing.gui.control.RealTimeControlPanel;
import nl.tudelft.simulation.language.DSOLException;

/**
 * GIS demo to show a map using an OpenStreepMap PBF file.
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OsmSwingApplication extends DSOLAnimationApplication
{
    /**
     * @param title String; the title
     * @param panel DSOLPanel; the panel
     * @param animationTab DSOLAnimationGisTab; the (custom) animation tab
     * @throws DSOLException when simulator is not an animator
     * @throws IllegalArgumentException for illegal bounds
     * @throws RemoteException on network error
     */
    public OsmSwingApplication(final String title, final DSOLPanel panel, final DSOLAnimationGisTab animationTab)
            throws RemoteException, IllegalArgumentException, DSOLException
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
     * @throws DSOLException when simulator is not an animator
     */
    public static void main(final String[] args) throws SimRuntimeException, RemoteException, NamingException, DSOLException
    {
        DevsxRealTimeAnimator.TimeDouble simulator = new DevsxRealTimeAnimator.TimeDouble("OSMSwingApplication", 0.001);
        EmptyModel model = new EmptyModel(simulator);
        ReplicationInterface<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 1000000.0);
        simulator.initialize(model, replication);

        DSOLPanel panel = new DSOLPanel(new RealTimeControlPanel.TimeDouble(model, simulator));
        Bounds2d mapBounds = new Bounds2d(4.355, 4.386, 51.995, 52.005);
        DSOLAnimationGisTab animationTab = new DSOLAnimationGisTab(mapBounds, simulator);
        animationTab.getAnimationPanel().setRenderableScale(
                new RenderableScale(Math.cos(Math.toRadians(mapBounds.midPoint().getY())), 1.0 / 111319.24));
        animationTab.addAllToggleGISButtonText("MAP LAYERS", model.getOsmMap(), "hide or show this GIS layer");
        new OsmSwingApplication("OSMSwingApplication", panel, animationTab);
    }

    /** The empty model -- this demo is just to show a map on the screen. */
    static class EmptyModel extends AbstractDSOLModel<Double, DevsSimulatorInterface<Double>>
    {
        /** The default serial version UID for serializable classes. */
        private static final long serialVersionUID = 1L;

        /** the GIS map. */
        private OsmRenderable2D gisMap;

        /**
         * constructs a new EmptyModel.
         * @param simulator DEVSSimulatorInterface&lt;Double&gt;; the simulator
         */
        EmptyModel(final DevsSimulatorInterface<Double> simulator)
        {
            super(simulator);
        }

        /** {@inheritDoc} */
        @Override
        public void constructModel() throws SimRuntimeException
        {
            URL csvUrl = URLResource.getResource("/resources/osm/tudelft.csv");
            System.out.println("GIS definitions file: " + csvUrl.toString());
            URL osmUrl = URLResource.getResource("/resources/osm/tudelft.osm.gz");
            System.out.println("GIS data file: " + osmUrl.toString());
            try
            {
                this.gisMap = new OsmRenderable2D(getSimulator().getReplication(),
                        OsmFileCsvParser.parseMapFile(csvUrl, osmUrl, "TU Delft"));
            }
            catch (IOException exception)
            {
                throw new SimRuntimeException(exception);
            }
        }

        /**
         * @return gisMap
         */
        public GisRenderable2D getOsmMap()
        {
            return this.gisMap;
        }
    }
}
