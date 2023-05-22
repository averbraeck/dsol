package nl.tudelft.simulation.examples.dsol.terminal;

import java.io.Serializable;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.event.EventType;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.model.AbstractDsolModel;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterInteger;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMap;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * A simple model of a container terminal. <br>
 * (c) copyright 2018 <a href="https://simulation.tudelft.nl">Delft University of Technology </a>, the Netherlands. <br>
 * See for project information <a href="https://simulation.tudelft.nl">www.simulation.tudelft.nl </a> <br>
 * License of use: <a href="http://www.gnu.org/copyleft/gpl.html">General Public License (GPL) </a>, no warranty <br>
 * @author <a href="http://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Terminal extends AbstractDsolModel<Double, DevsSimulatorInterface<Double>> implements EventListener
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** the ship-full event. */
    public static final EventType READY_EVENT = new EventType("READY_EVENT");

    /** QCs. */
    private int numQC = 5;

    /** AGVs. */
    private int numAGV = 25;

    /** replication nr. */
    private final int rep;

    /** debug info or not. */
    public static final boolean DEBUG = false;

    /** the ship for pub/sub when ready. */
    private Ship ship;

    /**
     * constructor for the Container Terminal.
     * @param simulator DEVSSimulatorInterface&lt;Double&gt;; the simulator
     * @param rep int; the replication number
     * @throws InputParameterException on input parameter error
     */
    public Terminal(final DevsSimulatorInterface<Double> simulator, final int rep) throws InputParameterException
    {
        super(simulator);
        this.rep = rep;
        this.inputParameterMap.add(new InputParameterInteger("numQC", "Number of QCs", "Number of Quay Cranes (QCs)", 10, 1.0));
        this.inputParameterMap.add(
                new InputParameterInteger("numAGV", "Number of AGVs", "Number of Automated Quided Vehicles (AGVs)", 20, 2.0));

    }

    /** {@inheritDoc} */
    @Override
    public void constructModel() throws SimRuntimeException
    {
        StreamInterface defaultStream = this.simulator.getModel().getStream("default");

        try
        {
            InputParameterMap parameters = this.simulator.getModel().getInputParameterMap();
            this.numQC = (Integer) parameters.get("numQC").getCalculatedValue();
            this.numAGV = (Integer) parameters.get("numAGV").getCalculatedValue();
        }
        catch (InputParameterException ipe)
        {
            throw new SimRuntimeException(ipe);
        }
        int numCont = 3000;

        QC qc = new QC(this.simulator, "QC", this.numQC, new DistExponential(defaultStream, 60. / 30.));
        AGV agv = new AGV(this.simulator, "AGV", this.numAGV, new DistTriangular(defaultStream, 7, 9, 14));
        this.ship = new Ship(numCont);

        this.ship.addListener(this, Ship.SHIP_FULL_EVENT);

        for (int c = 0; c < numCont; c++)
        {
            new Container(this.simulator, c, qc, agv, this.ship);
        }
    }

    /**
     * @return the ship for pub/sub
     */
    public Ship getShip()
    {
        return this.ship;
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final Event event)
    {
        if (event.getType().equals(Ship.SHIP_FULL_EVENT))
        {
            try
            {
                this.simulator.stop();
                double ready = this.simulator.getSimulatorTime() / 60.0;
                double delayHours = Math.max(0.0, Math.ceil(ready) - 20.0);
                if (DEBUG)
                {
                    System.out.println("Delay = " + delayHours);
                }
                double costs =
                        Math.max(20.0, Math.ceil(ready)) * (300.0 * this.numQC + 12.0 * this.numAGV) + 2500.0 * delayHours;
                if (DEBUG)
                {
                    System.out.println("Costs = " + costs);
                }
                int nrCont = (Integer) event.getContent();
                fireEvent(Terminal.READY_EVENT,
                        new Output(this.numQC, this.numAGV, this.rep, delayHours, costs, ready, nrCont));
            }
            catch (SimRuntimeException exception)
            {
                getSimulator().getLogger().always().error(exception);
            }
        }
    }

    /** */
    public static class Output implements Serializable
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** QCs. */
        private final int numQC;

        /** AGVs. */
        private final int numAGV;

        /** replication nr. */
        private final int rep;

        /** delay. */
        private final double delayHours;

        /** costs. */
        private final double costs;

        /** time when ready in hrs. */
        private final double ready;

        /** nr containers handled. */
        private final int nrCont;

        /**
         * /**
         * @param numQC int; qc
         * @param numAGV int; agv
         * @param rep int; replication nr
         * @param delayHours double; delay in hours
         * @param costs double; costs in Euros
         * @param ready double; time when ready in hrs
         * @param nrCont int; nr containers handled
         */
        public Output(final int numQC, final int numAGV, final int rep, final double delayHours, final double costs,
                final double ready, final int nrCont)
        {
            super();
            this.numQC = numQC;
            this.numAGV = numAGV;
            this.rep = rep;
            this.delayHours = delayHours;
            this.costs = costs;
            this.ready = ready;
            this.nrCont = nrCont;
        }

        /**
         * @return numQC
         */
        public int getNumQC()
        {
            return this.numQC;
        }

        /**
         * @return numAGV
         */
        public int getNumAGV()
        {
            return this.numAGV;
        }

        /**
         * @return rep
         */
        public int getRep()
        {
            return this.rep;
        }

        /**
         * @return delayHours
         */
        public double getDelayHours()
        {
            return this.delayHours;
        }

        /**
         * @return costs
         */
        public double getCosts()
        {
            return this.costs;
        }

        /**
         * @return ready
         */
        public double getReady()
        {
            return this.ready;
        }

        /**
         * @return nrCont
         */
        public int getNrCont()
        {
            return this.nrCont;
        }
    }

}
