package nl.tudelft.simulation.dsol.animation.D2;

import java.awt.Graphics2D;
import java.awt.MediaTracker;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.djutils.draw.bounds.Bounds2d;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;
import org.djutils.draw.point.Point3d;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.dsol.animation.StaticLocation3d;
import nl.tudelft.simulation.naming.context.Contextualized;

/**
 * An abstract class for state-dependent image renderables.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @since 1.5
 * @param <L> the Locatable class of the source that indicates the location of the Renderable on the screen
 */
public abstract class ImageRenderable<L extends Locatable> extends Renderable2D<L>
{
    /** */
    private static final long serialVersionUID = 20200108L;

    /** the cache of imageIcons. */
    private static transient Map<URL, ImageIcon> cache = new LinkedHashMap<URL, ImageIcon>();

    /** LEFT-BOTTOM location. */
    public static final short LB = -4;

    /** CENTER-BOTTOM location. */
    public static final short CB = -3;

    /** RIGHT-BOTTOM location. */
    public static final short RB = -2;

    /** LEFT-CENTER location. */
    public static final short LC = -1;

    /** CENTER-CENTER location. */
    public static final short CC = 0;

    /** RIGHT-CENTER location. */
    public static final short RC = 1;

    /** LEFT-TOP location. */
    public static final short LT = 2;

    /** CENTER-TOP location. */
    public static final short CT = 3;

    /** RIGHT-TOP location. */
    public static final short RT = 4;

    /** the imageIcons to use. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected URL[] imageUrls = null;

    /** the imageIcons to be used. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected transient ImageIcon[] imageIcons = null;

    /** the origin of the image. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected short orientation = ImageRenderable.CC;

    /**
     * constructs a new ImageRenderable.
     * @param source T; the source to be animated.
     * @param contextProvider Contextualized; the object that can provide the context to store the animation objects
     * @param images URL[]; the image urls.
     */
    public ImageRenderable(final L source, final Contextualized contextProvider, final URL[] images)
    {
        super(source, contextProvider);
        this.setOrientation(ImageRenderable.CC);
        this.readImages(images);
    }

    /**
     * reads and caches the images.
     * @param images URL[]; the images
     */
    private void readImages(final URL[] images)
    {
        this.imageUrls = images;
        this.imageIcons = new ImageIcon[images.length];
        for (int i = 0; i < images.length; i++)
        {
            if (ImageRenderable.cache.containsKey(images[i]))
            {
                this.imageIcons[i] = ImageRenderable.cache.get(images[i]);
            }
            else
            {
                this.imageIcons[i] = new ImageIcon(images[i]);
                ImageRenderable.cache.put(images[i], this.imageIcons[i]);
            }
        }
    }

    /**
     * constructs a new ImageRenderable.
     * @param staticLocation OrientedPoint3d; the static location of the set of imageIcons
     * @param size Bounds3; the size of the imageIcons in world coordinates.
     * @param contextProvider Contextualized; the object that can provide the context to store the animation objects
     * @param images URL[]; the imageIcons to display.
     */
    @SuppressWarnings("unchecked")
    public ImageRenderable(final OrientedPoint3d staticLocation, final Bounds3d size, final Contextualized contextProvider,
            final URL[] images)
    {
        this((L) new StaticLocation3d(staticLocation, size), contextProvider, images);
    }

    /**
     * constructs a new ImageRenderable.
     * @param staticLocation Point3d; the static location of the set of imageIcons
     * @param size Bounds3d; the size of the imageIcons in world coordinates.
     * @param contextProvider Contextualized; the object that can provide the context to store the animation objects
     * @param images URL[]; the imageIcons to display.
     */
    @SuppressWarnings("unchecked")
    public ImageRenderable(final Point3d staticLocation, final Bounds3d size, final Contextualized contextProvider,
            final URL[] images)
    {
        this((L) new StaticLocation3d(new OrientedPoint3d(staticLocation.getX(), staticLocation.getY(), 0.0),
                new Bounds3d(size.getDeltaX(), size.getDeltaY(), 0.0)), contextProvider, images);
    }

    /** {@inheritDoc} */
    @Override
    public void paint(final Graphics2D graphics, final ImageObserver observer)
    {
        try
        {
            if (getSource().getLocation() == null)
            {
                return;
            }
            int image = this.selectImage();
            if (this.imageIcons == null || this.imageIcons[image] == null
                    || this.imageIcons[image].getImageLoadStatus() != MediaTracker.COMPLETE)
            {
                return;
            }
            Bounds2d size = BoundsUtil.projectBounds(getSource().getLocation(), getSource().getBounds());
            Point2D origin = this.resolveOrigin(this.orientation, size);
            graphics.translate(origin.getX(), origin.getY());
            graphics.scale(0.001, 0.001);
            graphics.drawImage(this.imageIcons[image].getImage(), 0, 0, (int) (1000 * size.getDeltaX()),
                    (int) (1000 * size.getDeltaY()), observer);
            graphics.scale(1000, 1000);
            graphics.translate(-origin.getX(), -origin.getY());
        }
        catch (RemoteException exception)
        {
            CategoryLogger.always().warn(exception);
        }
    }

    /**
     * selects the image. This methods makes the ImageRenderable state dependent. One is required to return the index number of
     * the imageIcons[] which has to be drawn.
     * @return int the current (state-dependent) image.
     */
    public abstract int selectImage();

    /**
     * @param orientation short; The orientation to set.
     */
    public void setOrientation(final short orientation)
    {
        this.orientation = orientation;
    }

    /**
     * @return Returns the imageIcons.
     */
    public ImageIcon[] getImages()
    {
        return this.imageIcons;
    }

    /**
     * resolves the origin of the image.
     * @param forOrientation short; the orientation (CC,..)
     * @return Bounds2d the location
     * @param size Dimension; the size of the image.
     */
    protected Point2D resolveOrigin(final short forOrientation, final Bounds2d size)
    {
        Point2D imageOrigin = new Point2D.Double(0.0, 0.0);
        switch (forOrientation)
        {
            case ImageRenderable.LB:
                imageOrigin.setLocation(imageOrigin.getX(), imageOrigin.getY() - size.getDeltaY());
                return imageOrigin;
            case ImageRenderable.CB:
                imageOrigin.setLocation(imageOrigin.getX() - 0.5 * size.getDeltaX(), imageOrigin.getY() - size.getDeltaY());
                return imageOrigin;
            case ImageRenderable.RB:
                imageOrigin.setLocation(imageOrigin.getX() - 1.0 * size.getDeltaX(), imageOrigin.getY() - size.getDeltaY());
                return imageOrigin;
            case ImageRenderable.LC:
                imageOrigin.setLocation(imageOrigin.getX(), imageOrigin.getY() - 0.5 * size.getDeltaY());
                return imageOrigin;
            case ImageRenderable.CC:
                imageOrigin.setLocation(imageOrigin.getX() - 0.5 * size.getDeltaX(),
                        imageOrigin.getY() - 0.5 * size.getDeltaY());
                return imageOrigin;
            case ImageRenderable.RC:
                imageOrigin.setLocation(imageOrigin.getX() - 1.0 * size.getDeltaX(),
                        imageOrigin.getY() - 0.5 * size.getDeltaY());
                return imageOrigin;
            case ImageRenderable.LT:
                imageOrigin.setLocation(imageOrigin.getX(), imageOrigin.getY());
                return imageOrigin;
            case ImageRenderable.CT:
                imageOrigin.setLocation(imageOrigin.getX() - 0.5 * size.getDeltaX(), imageOrigin.getY());
                return imageOrigin;
            case ImageRenderable.RT:
                imageOrigin.setLocation(imageOrigin.getX() - 1.0 * size.getDeltaX(), imageOrigin.getY());
                return imageOrigin;
            default:
                throw new IllegalArgumentException("unknown origin location");
        }
    }

    /**
     * Returns the orientation of this image to the point [0,0]. Orientations are either LEFT, CENTER, RIGHT and TOP, CENTER, or
     * BOTTOM. An example is thus ImageRenderable.RT.
     * @return the orientation of this image
     */
    public short getOrientation()
    {
        return this.orientation;
    }

    /**
     * writes a serializable object to stream.
     * @param out ObjectOutputStream; the outputstream
     * @throws IOException on IOException
     */
    private synchronized void writeObject(final ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();
    }

    /**
     * reads a serializable object from stream.
     * @param in java.io.ObjectInputStream; the inputstream
     * @throws IOException on IOException
     * @throws ClassNotFoundException if the class of a serialized objectcould not be found
     */
    private synchronized void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        if (ImageRenderable.cache == null)
        {
            ImageRenderable.cache = new LinkedHashMap<URL, ImageIcon>();
        }
        this.readImages(this.imageUrls);
    }
}
