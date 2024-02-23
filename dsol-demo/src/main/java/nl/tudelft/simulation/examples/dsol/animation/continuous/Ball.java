package nl.tudelft.simulation.examples.dsol.animation.continuous;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.dsol.simulators.DessSimulatorInterface;
import nl.tudelft.simulation.examples.dsol.animation.BallAnimation;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.language.d3.CartesianPoint;

/**
 * <p>
 * Copyright (c) 2002- 2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <p>
 * See for project information <a href="https://simulation.tudelft.nl/" target="_blank"> www.simulation.tudelft.nl</a>.
 * <p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Ball extends nl.tudelft.simulation.examples.dsol.animation.Ball
{
    /** the positioner. */
    private Positioner positioner = null;

    /** the origin. */
    private CartesianPoint origin = new CartesianPoint(0, 0, 0);

    /** the destination. */
    private CartesianPoint destination = new CartesianPoint(0, 0, 0);

    /** the rotation. */
    private double angle = 0.0;

    /** the simulator to use. */
    private DessSimulatorInterface<Double> simulator = null;

    /**
     * constructs a new Ball.
     * @param nr int; the ball number
     * @param simulator DessSimulatorInterface&lt;Double&gt;; the simulator
     * @throws RemoteException on network exception
     * @throws NamingException on animation error
     */
    public Ball(final int nr, final DessSimulatorInterface<Double> simulator) throws RemoteException, NamingException
    {
        super(nr);
        this.simulator = simulator;
        this.positioner = new Positioner(simulator);
        new BallAnimation(this, simulator);
        try
        {
            this.next();
        }
        catch (RemoteException exception)
        {
            simulator.getLogger().always().error(exception);
        }
    }

    /** {@inheritDoc} */
    @Override
    public OrientedPoint3d getLocation() throws RemoteException
    {
        double x = Math.cos(this.angle) * this.positioner.y(this.simulator.getSimulatorTime())[1] + this.origin.getX();
        double y = Math.sin(this.angle) * this.positioner.y(this.simulator.getSimulatorTime())[1] + this.origin.getY();
        if (Math.abs(x - this.origin.getX()) > Math.abs(this.destination.getX() - this.origin.getX())
                || Math.abs(y - this.origin.getY()) > Math.abs(this.destination.getY() - this.origin.getY()))
        {
            this.next();
        }
        return new OrientedPoint3d(x, y, 0, 0.0, 0.0, this.theta);
    }

    /**
     * next move.
     * @throws RemoteException on network failure
     */
    public void next() throws RemoteException
    {
        StreamInterface stream = this.simulator.getModel().getStream("default");
        this.origin = this.destination;
        this.positioner.setValue(0);
        this.destination = new CartesianPoint(-100 + stream.nextInt(0, 200), -100 + stream.nextInt(0, 200), 0);
        this.angle = (this.destination.getY() - this.origin.getY()) / (this.destination.getX() - this.origin.getX());
    }
}
