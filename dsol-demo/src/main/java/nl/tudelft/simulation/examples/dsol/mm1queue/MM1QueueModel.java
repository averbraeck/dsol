package nl.tudelft.simulation.examples.dsol.mm1queue;

import org.djutils.stats.summarizers.event.StatisticsEvents;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.Resource;
import nl.tudelft.simulation.dsol.formalisms.flow.Delay;
import nl.tudelft.simulation.dsol.formalisms.flow.Generate;
import nl.tudelft.simulation.dsol.formalisms.flow.Station;
import nl.tudelft.simulation.dsol.model.AbstractDSOLModel;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterDouble;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterInteger;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMap;
import nl.tudelft.simulation.dsol.simtime.dist.DistContinuousSimulationTime;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;
import nl.tudelft.simulation.dsol.statistics.SimCounter;
import nl.tudelft.simulation.dsol.statistics.SimPersistent;
import nl.tudelft.simulation.dsol.swing.charts.boxAndWhisker.BoxAndWhiskerChart;
import nl.tudelft.simulation.dsol.swing.charts.histogram.Histogram;
import nl.tudelft.simulation.dsol.swing.charts.xy.XYChart;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteConstant;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * The M/M/1 example as published in Simulation Modeling and Analysis by A.M. Law &amp; W.D. Kelton section 1.4 and 2.4. .
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="http://www.tbm.tudelft.nl/webstaf/peterja/index.htm">Peter Jacobs </a>
 */
public class MM1QueueModel extends AbstractDSOLModel<Double, DevsSimulator<Double>>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** the chart for the service time. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected XYChart serviceTimeChart;

    /** the Box-and-Whisher chart for the service time. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected BoxAndWhiskerChart serviceTimeBWChart;

    /**
     * constructor for the MM1Queue.
     * @param simulator DEVSSimulator&lt;Double&gt;; the simulator
     * @throws InputParameterException on parameter error
     */
    public MM1QueueModel(final DevsSimulator<Double> simulator) throws InputParameterException
    {
        super(simulator);
        InputParameterMap generatorMap = new InputParameterMap("generator", "Generator", "Generator", 1.0);
        generatorMap.add(new InputParameterDouble("intervalTime", "Average interval time", "Average interval time", 1.0, 1.0));
        generatorMap.add(new InputParameterDouble("startTime", "Generator start time", "Generator start time", 0.0, 2.0));
        generatorMap.add(new InputParameterInteger("batchSize", "Batch size", "batch size", 1, 3.0));
        this.inputParameterMap.add(generatorMap);
        InputParameterMap resourceMap = new InputParameterMap("resource", "Resource", "Resource", 2.0);
        resourceMap.add(new InputParameterDouble("capacity", "Resource capacity", "Resource capacity", 1.0, 1.0));
        resourceMap.add(new InputParameterDouble("serviceTime", "Average service time", "Average service time", 0.8, 2.0));
        this.inputParameterMap.add(resourceMap);

    }

    /** {@inheritDoc} */
    @Override
    public void constructModel() throws SimRuntimeException
    {
        StreamInterface defaultStream = getStream("default");

        try
        {
            InputParameterMap parameters = this.simulator.getModel().getInputParameterMap();

            // The Generator
            Generate<Double> generator = new Generate<Double>("Generator", this.simulator, Customer.class, null);
            DistContinuousSimulationTime<Double> intervalTime = new DistContinuousSimulationTime.TimeDouble(
                    new DistExponential(defaultStream, (Double) parameters.get("generator.intervalTime").getCalculatedValue()));
            generator.setInterval(intervalTime);

            DistContinuousSimulationTime<Double> startTime = new DistContinuousSimulationTime.TimeDouble(
                    new DistConstant(defaultStream, (Double) parameters.get("generator.startTime").getCalculatedValue()));
            generator.setStartTime(startTime);

            DistDiscrete batchSize = new DistDiscreteConstant(defaultStream,
                    (Integer) parameters.get("generator.batchSize").getCalculatedValue());
            generator.setBatchSize(batchSize);

            // The queue, the resource and the release
            double capacity = (Double) parameters.get("resource.capacity").getCalculatedValue();
            Resource<Double> resource = new Resource<>(this.simulator, capacity);

            // created a resource
            Station<Double> queue = new Seize("Seize", this.simulator, resource);
            Station<Double> release = new Release("Release", this.simulator, resource, capacity);

            // The server
            DistContinuousSimulationTime<Double> serviceTime = new DistContinuousSimulationTime.TimeDouble(
                    new DistExponential(defaultStream, (Double) parameters.get("resource.serviceTime").getCalculatedValue()));
            Station<Double> server = new Delay<Double>("Delay", this.simulator, serviceTime);

            // The flow
            generator.setDestination(queue);
            queue.setDestination(server);
            server.setDestination(release);

            // Statistics

            new SimCounter<Double>("counting the generator", this, generator, Generate.CREATE_EVENT);
            SimPersistent<Double> persistent =
                    new SimPersistent<Double>("persistent on service time", this, release, Release.SERVICE_TIME_EVENT);

            Histogram histogram = new Histogram(this.simulator, "histogram on service time", new double[] {0, 10}, 30);
            histogram.add("histogram on service time", persistent, StatisticsEvents.SAMPLE_MEAN_EVENT);

            this.serviceTimeChart = new XYChart(this.simulator, "XY chart of service time",
                    new double[] {0, this.simulator.getReplication().getRunLength()}, new double[] {-2, 30});
            this.serviceTimeChart.add(persistent);

            this.serviceTimeBWChart = new BoxAndWhiskerChart(this.simulator, "BoxAndWhisker on serviceTime");
            this.serviceTimeBWChart.add(persistent);
        }
        catch (Exception exception)
        {
            throw new SimRuntimeException(exception);
        }
    }

}
