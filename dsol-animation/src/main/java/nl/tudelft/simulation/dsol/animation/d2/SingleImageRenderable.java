package nl.tudelft.simulation.dsol.animation.d2;

import java.net.URL;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;
import org.djutils.draw.point.Point3d;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.naming.context.Contextualized;

/**
 * Render an image on screen where there is only a single image.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @since 1.5
 * @param <L> the Locatable class of the source that indicates the location of the Renderable on the screen
 */
public class SingleImageRenderable<L extends Locatable> extends ImageRenderable<L>
{
    /** */
    private static final long serialVersionUID = 20200108L;

    /**
     * constructs a new SingleImageRenderable.
     * @param source the moving source
     * @param contextProvider the object that can provide the context to store the animation objects
     * @param image the image to animate
     * @throws NamingException when animation context cannot be created or retrieved
     * @throws RemoteException when remote context cannot be found
     */
    public SingleImageRenderable(final L source, final Contextualized contextProvider, final URL image)
            throws RemoteException, NamingException
    {
        super(source, contextProvider, new URL[] {image});
    }

    /**
     * constructs a new SingleImageRenderable.
     * @param staticLocation the static location
     * @param size the size
     * @param contextProvider the object that can provide the context to store the animation objects
     * @param image the image
     * @throws NamingException when animation context cannot be created or retrieved
     * @throws RemoteException when remote context cannot be found
     */
    public SingleImageRenderable(final Point3d staticLocation, final Bounds3d size, final Contextualized contextProvider,
            final URL image) throws RemoteException, NamingException
    {
        super(staticLocation, size, contextProvider, new URL[] {image});
    }

    /**
     * constructs a new SingleImageRenderable.
     * @param staticLocation the static location
     * @param size the size of the image
     * @param contextProvider the object that can provide the context to store the animation objects
     * @param image the image
     * @throws NamingException when animation context cannot be created or retrieved
     * @throws RemoteException when remote context cannot be found
     */
    public SingleImageRenderable(final OrientedPoint3d staticLocation, final Bounds3d size,
            final Contextualized contextProvider, final URL image) throws RemoteException, NamingException
    {
        super(staticLocation, size, contextProvider, new URL[] {image});
    }

    @Override
    public int selectImage()
    {
        // We only have one image to show. Let's use this one.
        return 0;
    }
}
