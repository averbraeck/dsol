package nl.tudelft.simulation.dsol.web.test.gis;

import java.io.Serializable;
import java.net.URL;

import org.djutils.io.URLResource;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;
import nl.tudelft.simulation.dsol.simulators.DEVSRealTimeAnimator;

/**
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 */
public class GISModel extends AbstractDSOLModel<Double, DEVSRealTimeAnimator.TimeDouble>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /**
     * constructs a new GISModel.
     * @param simulator DEVSRealTimeClock&lt;Double&gt;; the simulator
     */
    public GISModel(final DEVSRealTimeAnimator.TimeDouble simulator)
    {
        super(simulator);
    }

    /** {@inheritDoc} */
    @Override
    public void constructModel() throws SimRuntimeException
    {
        URL gisURL = URLResource.getResource("/resources/gis/map.xml");
        System.err.println("GIS-map file: " + gisURL.toString());
        new GisRenderableNoCache2D(this.simulator, gisURL);
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return "GISModel";
    }
}
