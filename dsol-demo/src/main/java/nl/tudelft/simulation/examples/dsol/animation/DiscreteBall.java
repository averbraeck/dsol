package nl.tudelft.simulation.examples.dsol.animation;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistUniform;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.language.d3.CartesianPoint;

/**
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <p>
 * See for project information <a href="https://simulation.tudelft.nl/" target="_blank"> www.simulation.tudelft.nl</a>.
 * <p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class DiscreteBall extends Ball
{
    /** the origin. */
    private CartesianPoint origin = new CartesianPoint(0, 0, 0);

    /** the destination. */
    private CartesianPoint destination = new CartesianPoint(0, 0, 0);

    /** the simulator. */
    private DevsSimulatorInterface<Double> simulator = null;

    /** the start time. */
    private double startTime = Double.NaN;

    /** the stop time. */
    private double stopTime = Double.NaN;

    /** the stream -- ugly but works. */
    private static StreamInterface stream = new MersenneTwister();

    /**
     * constructs a new Ball.
     * @param nr the ball number
     * @param simulator the simulator
     * @throws RemoteException on remote failure
     * @throws SimRuntimeException on schedule failure
     */
    public DiscreteBall(final int nr, final DevsSimulatorInterface<Double> simulator)
            throws RemoteException, SimRuntimeException
    {
        super(nr);
        this.simulator = simulator;
        // URL image = URLResource.getResource("/nl/tudelft/simulation/examples/dsol/animation/images/customer.jpg");
        // new SingleImageRenderable(this, simulator, image);
        try
        {
            new BallAnimation(this, simulator);
        }
        catch (NamingException exception)
        {
            this.simulator.getLogger().always().error(exception);
        }
        this.next();
    }

    /**
     * next movement.
     * @throws RemoteException on network failure
     * @throws SimRuntimeException on simulation failure
     */
    private void next() throws RemoteException, SimRuntimeException
    {
        this.origin = this.destination;
        this.destination = new CartesianPoint(-100 + stream.nextInt(0, 200), -100 + stream.nextInt(0, 200), 0);
        this.startTime = this.simulator.getSimulatorTime();
        this.stopTime = this.startTime + Math.abs(new DistUniform(stream, 2.0, 20.0).draw());
        this.simulator.scheduleEventAbs(this.stopTime, this, "next", null);
    }

    @Override
    public OrientedPoint3d getLocation() throws RemoteException
    {
        double fraction = (this.simulator.getSimulatorTime() - this.startTime) / (this.stopTime - this.startTime);
        double x = this.origin.getX() + (this.destination.getX() - this.origin.getX()) * fraction;
        double y = this.origin.getY() + (this.destination.getY() - this.origin.getY()) * fraction;
        return new OrientedPoint3d(x, y, 0, 0.0, 0.0, this.theta);
    }
}
