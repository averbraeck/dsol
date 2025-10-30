package nl.tudelft.simulation.dsol.web.test.gis;

import java.net.URL;

import org.djutils.io.ResourceResolver;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.simulators.DevsRealTimeAnimator;

/**
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 */
public class GISModel extends AbstractDsolModel<Double, DevsRealTimeAnimator.TimeDouble>
{
    /**
     * constructs a new GISModel.
     * @param simulator the simulator
     */
    public GISModel(final DevsRealTimeAnimator.TimeDouble simulator)
    {
        super(simulator);
    }

    @Override
    public void constructModel() throws SimRuntimeException
    {
        URL gisURL = ResourceResolver.resolve("/resources/gis/map.xml").asUrl();
        System.err.println("GIS-map file: " + gisURL.toString());
        new GisRenderableNoCache2D(this.simulator, gisURL);
    }
}
