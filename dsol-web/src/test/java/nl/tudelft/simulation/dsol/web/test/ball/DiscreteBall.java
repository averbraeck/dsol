package nl.tudelft.simulation.dsol.web.test.ball;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistNormal;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.language.d3.CartesianPoint;

/**
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <p>
 * See for project information <a href="https://simulation.tudelft.nl/" target="_blank"> www.simulation.tudelft.nl</a>.
 * <p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DiscreteBall extends Ball
{
    /** the ball number. */
    private final int nr;

    /** the origin. */
    private CartesianPoint origin = new CartesianPoint(0, 0, 0);

    /** the destination. */
    private CartesianPoint destination = new CartesianPoint(0, 0, 0);

    /** the simulator. */
    private DEVSSimulatorInterface<Double> simulator = null;

    /** the start time. */
    private double startTime = Double.NaN;

    /** the stop time. */
    private double stopTime = Double.NaN;

    /** the stream -- ugly but works. */
    private static StreamInterface stream = new MersenneTwister();

    /**
     * constructs a new Ball.
     * @param simulator DEVSSimulatorInterface&lt;Double&gt;; the simulator
     * @param nr int; the ball number
     * @throws RemoteException on remote failure
     * @throws SimRuntimeException on schedule failure
     */
    public DiscreteBall(final DEVSSimulatorInterface<Double> simulator, final int nr)
            throws RemoteException, SimRuntimeException
    {
        super();
        this.nr = nr;
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
        System.out.println("t=" + this.simulator.getSimulatorTime() + ": ball nr " + this.nr + " bounced");
        this.origin = this.destination;
        this.destination = new CartesianPoint(-100 + stream.nextInt(0, 200), -100 + stream.nextInt(0, 200), 0);
        this.startTime = this.simulator.getSimulatorTime();
        this.stopTime = this.startTime + Math.abs(new DistNormal(stream, 9, 1.8).draw());
        this.simulator.scheduleEventAbs(this.stopTime, this, this, "next", null);
    }

    /** {@inheritDoc} */
    @Override
    public OrientedPoint3d getLocation() throws RemoteException
    {
        double fraction = (this.simulator.getSimulatorTime() - this.startTime) / (this.stopTime - this.startTime);
        double x = this.origin.getX() + (this.destination.getX() - this.origin.getX()) * fraction;
        double y = this.origin.getY() + (this.destination.getY() - this.origin.getY()) * fraction;
        return new OrientedPoint3d(x, y, 0, 0.0, 0.0, this.theta);
    }
}
