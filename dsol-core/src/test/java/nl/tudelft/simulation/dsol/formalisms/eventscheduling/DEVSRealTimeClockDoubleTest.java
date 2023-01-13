package nl.tudelft.simulation.dsol.formalisms.eventscheduling;

import java.rmi.RemoteException;
import java.util.concurrent.TimeoutException;

import javax.naming.NamingException;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.junit.Test;

import net.jodah.concurrentunit.Waiter;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.ReplicationInterface;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;
import nl.tudelft.simulation.dsol.simulators.DEVSRealTimeAnimator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistUniform;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DEVSRealTimeClockDoubleTest implements EventListener
{
    /** */
    private static final long serialVersionUID = 1L;

    /** */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected DevsSimulatorInterface<Double> devsSimulator;

    /** the Waiter from ConcurrentUnit that catches AssertionErrors in other threads. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Waiter waiter;

    /**
     * @throws SimRuntimeException in case of error
     * @throws RemoteException in case of error
     * @throws NamingException in case of error
     * @throws InterruptedException on error
     * @throws TimeoutException on error
     */
    @Test
    public void testDEVSRealTimeClockDouble()
            throws SimRuntimeException, RemoteException, NamingException, TimeoutException, InterruptedException
    {
        this.waiter = new Waiter();
        this.devsSimulator = new DEVSRealTimeAnimator.TimeDouble("testDEVSSimulationDouble", 0.1);
        this.devsSimulator.addListener(this, ReplicationInterface.END_REPLICATION_EVENT);
        ModelDouble model = new ModelDouble(this.devsSimulator);
        ReplicationInterface<Double> rep = new SingleReplication<Double>("rep1", 0.0, 0.0, 100.0);
        this.devsSimulator.initialize(model, rep);
        this.devsSimulator.scheduleEventAbs(1.0, this, "step1", new Object[] {1.0});
        this.devsSimulator.start();
        this.waiter.await(10000);
    }

    /**
     * Do a simulation step and check the time.
     * @param checkTime the step on the simulator
     * @throws SimRuntimeException on error
     */
    protected void step1(final double checkTime) throws SimRuntimeException
    {
        this.waiter.assertEquals(this.devsSimulator.getSimulatorTime(), checkTime, 0.0001);
        this.devsSimulator.scheduleEventRel(1.0, this, "step1", new Object[] {checkTime + 1.0});
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final Event event) throws RemoteException
    {
        this.waiter.resume();
    }

    /**
     * @throws SimRuntimeException in case of error
     * @throws RemoteException in case of error
     * @throws NamingException in case of error
     * @throws InterruptedException on error
     * @throws TimeoutException on error
     */
    // TODO: @Test -- this one is not always ready in 60 secs
    public void testRunUpTo()
            throws SimRuntimeException, RemoteException, NamingException, TimeoutException, InterruptedException
    {
        this.waiter = new Waiter();
        this.devsSimulator = new DEVSRealTimeAnimator.TimeDouble("testRunUpTo", 0.1);
        this.devsSimulator.addListener(this, ReplicationInterface.END_REPLICATION_EVENT);
        ModelDouble model = new ModelDouble(this.devsSimulator);
        ReplicationInterface<Double> rep = new SingleReplication<Double>("rep1", 0.0, 0.0, 1000.0);
        this.devsSimulator.initialize(model, rep);
        final DevsSimulatorInterface<Double> sim = this.devsSimulator;
        final Waiter w = this.waiter;
        final Object target = this;

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                StreamInterface stream = new MersenneTwister();
                DistContinuous dist = new DistUniform(stream, 0, 1000);
                for (int i = 0; i < 10000; i++)
                {
                    double time = dist.draw();
                    sim.scheduleEventAbs(time, target, "doWork", new Object[] {time});
                }
                for (double t = 0.0; t < 1000.0; t += 1.0)
                {
                    // System.out.println(t);
                    try
                    {
                        Thread.sleep(2, 0);
                    }
                    catch (InterruptedException exception)
                    {
                        System.err.println("Interrupt run!");
                        w.fail(exception);
                    }

                    sim.runUpTo(t);
                    while (sim.isStartingOrRunning())
                    {
                        try
                        {
                            Thread.sleep(0, 1);
                        }
                        catch (InterruptedException exception)
                        {
                            System.err.println("Interrupt run!");
                            w.fail(exception);
                        }
                    }
                    w.assertEquals(t, sim.getSimulatorTime(), 0.0001);
                }
                sim.start();
            }
        }).start();
        this.waiter.await(60000);
    }

    /** the distribution for the work time. 1 in 1000 events take 0.1 sec. */
    private DistContinuous workTimeDist = new DistUniform(new MersenneTwister(200L), 0, 1000);

    /**
     * do some work.
     * @param time the expected time when the event should be executed
     */
    protected void doWork(final double time)
    {
        this.waiter.assertEquals(time, this.devsSimulator.getSimulatorTime(), 0.0001);
        // sometimes the work takes time: 1 in 1000 events takes 0.05 sec
        // there are 100000 events; 100 * 0.05 sec = 5 sec
        try
        {
            if ((int) this.workTimeDist.draw() == 500)
            {
                Thread.sleep(50);
            }
        }
        catch (InterruptedException exception)
        {
            System.err.println("Interrupt doWork!");
            this.waiter.fail("doWork interrupted");
        }
    }

    /**
     * @throws SimRuntimeException in case of error
     * @throws RemoteException in case of error
     * @throws NamingException in case of error
     * @throws InterruptedException on error
     * @throws TimeoutException on error
     */
    @Test
    public void testSimLambda()
            throws SimRuntimeException, RemoteException, NamingException, TimeoutException, InterruptedException
    {
        this.waiter = new Waiter();
        this.devsSimulator = new DevsSimulator<Double>("testSimLambda");
        this.devsSimulator.addListener(this, ReplicationInterface.END_REPLICATION_EVENT);
        ModelDouble model = new ModelDouble(this.devsSimulator);
        ReplicationInterface<Double> rep = new SingleReplication<Double>("rep1", 0.0, 0.0, 100.0);
        this.devsSimulator.initialize(model, rep);

        for (int i = 0; i < 10; i++)
        {
            this.devsSimulator.scheduleEventAbs(1.0d * i, new Executable()
            {
                @Override
                public void execute()
                {
                    DEVSRealTimeClockDoubleTest.this.waiter
                            .assertTrue(DEVSRealTimeClockDoubleTest.this.devsSimulator.getSimulatorTime() <= 10.0);
                }
            });
        }
        this.devsSimulator.start();
        this.waiter.await(20000);
    }

    /**
     * THE MODEL.
     */
    public static class ModelDouble extends AbstractDSOLModel<Double, DevsSimulatorInterface<Double>>
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * @param simulator the simulator.
         */
        public ModelDouble(final DevsSimulatorInterface<Double> simulator)
        {
            super(simulator);
        }

        /** {@inheritDoc} */
        @Override
        public void constructModel() throws SimRuntimeException
        {
            //
        }
    }
}
