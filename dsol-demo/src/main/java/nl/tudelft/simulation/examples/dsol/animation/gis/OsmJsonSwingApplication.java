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
import nl.tudelft.simulation.dsol.animation.gis.osm.OsmFileJsonParser;
import nl.tudelft.simulation.dsol.animation.gis.osm.OsmRenderable2d;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsRealTimeAnimator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.dsol.swing.gui.DsolPanel;
import nl.tudelft.simulation.dsol.swing.gui.animation.DsolAnimationApplication;
import nl.tudelft.simulation.dsol.swing.gui.animation.DsolAnimationGisTab;
import nl.tudelft.simulation.dsol.swing.gui.control.RealTimeControlPanel;
import nl.tudelft.simulation.language.DsolException;

/**
 * GIS demo to show a map using an OpenStreepMap PBF file.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class OsmJsonSwingApplication extends DsolAnimationApplication
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param title the title
     * @param panel the panel
     * @param animationTab the (custom) animation tab
     * @throws DsolException when simulator is not an animator
     * @throws IllegalArgumentException for illegal bounds
     */
    public OsmJsonSwingApplication(final String title, final DsolPanel panel, final DsolAnimationGisTab animationTab)
            throws IllegalArgumentException, DsolException
    {
        super(panel, title, animationTab);
        panel.enableSimulationControlButtons();
    }

    /**
     * @param args arguments, expected to be empty
     * @throws SimRuntimeException on error
     * @throws RemoteException on error
     * @throws NamingException on error
     * @throws DsolException when simulator is not an animator
     */
    public static void main(final String[] args) throws SimRuntimeException, RemoteException, NamingException, DsolException
    {
        DevsRealTimeAnimator.TimeDouble simulator = new DevsRealTimeAnimator.TimeDouble("sim", 0.001);
        EmptyModel model = new EmptyModel(simulator);
        Replication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 1000000.0);
        simulator.initialize(model, replication);

        DsolPanel panel = new DsolPanel(new RealTimeControlPanel.TimeDouble(model, simulator));
        Bounds2d mapBounds = new Bounds2d(4.355, 4.386, 51.995, 52.005);
        DsolAnimationGisTab animationTab = new DsolAnimationGisTab(mapBounds, simulator);
        animationTab.getAnimationPanel().setRenderableScale(
                new RenderableScale(Math.cos(Math.toRadians(mapBounds.midPoint().getY())), 1.0 / 111319.24));
        animationTab.getAnimationPanel().setShowGrid(false);
        animationTab.addAllToggleGISButtonText("MAP LAYERS", model.getOsmMap(), "hide or show this GIS layer");
        new OsmJsonSwingApplication("OSMJsonSwingApplication", panel, animationTab);
    }

    /** The empty model -- this demo is just to show a map on the screen. */
    static class EmptyModel extends AbstractDsolModel<Double, DevsSimulatorInterface<Double>>
    {
        /** the GIS map. */
        private OsmRenderable2d gisMap;

        /**
         * constructs a new EmptyModel.
         * @param simulator the simulator
         */
        EmptyModel(final DevsSimulatorInterface<Double> simulator)
        {
            super(simulator);
        }

        @Override
        public void constructModel() throws SimRuntimeException
        {
            URL jsonUrl = URLResource.getResource("/resources/osm/tudelft.json");
            System.out.println("GIS definitions file: " + jsonUrl.toString());
            URL osmUrl = URLResource.getResource("/resources/osm/tudelft.osm.gz");
            System.out.println("GIS data file: " + osmUrl.toString());
            try
            {
                this.gisMap = new OsmRenderable2d(getSimulator().getReplication(),
                        OsmFileJsonParser.parseMapFile(jsonUrl, osmUrl, "TU Delft"));
            }
            catch (IOException exception)
            {
                throw new SimRuntimeException(exception);
            }
        }

        /**
         * @return gisMap
         */
        public GisRenderable2d getOsmMap()
        {
            return this.gisMap;
        }
    }
}
