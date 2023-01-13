package nl.tudelft.simulation.dsol.web.test.mm1;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.SingleReplication;
import nl.tudelft.simulation.dsol.simulators.DevsSimulator;

/**
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <p>
 * See for project information <a href="https://simulation.tudelft.nl/" target="_blank"> www.simulation.tudelft.nl</a>.
 * <p>
 * The DSOL project is distributed under the following BSD-style license:<br>
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 * <ul>
 * <li>Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.</li>
 * <li>Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.</li>
 * <li>Neither the name of Delft University of Technology, nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.</li>
 * </ul>
 * This software is provided by the copyright holders and contributors "as is" and any express or implied warranties, including,
 * but not limited to, the implied warranties of merchantability and fitness for a particular purpose are disclaimed. In no
 * event shall the copyright holder or contributors be liable for any direct, indirect, incidental, special, exemplary, or
 * consequential damages (including, but not limited to, procurement of substitute goods or services; loss of use, data, or
 * profits; or business interruption) however caused and on any theory of liability, whether in contract, strict liability, or
 * tort (including negligence or otherwise) arising in any way out of the use of this software, even if advised of the
 * possibility of such damage.
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class WebMM1Queue41Application
{
    /** */
    private DevsSimulator<Double> simulator;

    /** */
    private WebMM1Queue41Model model;

    /**
     * Construct a console application.
     * @throws SimRuntimeException on error
     * @throws RemoteException on error
     * @throws NamingException on error
     */
    protected WebMM1Queue41Application() throws SimRuntimeException, RemoteException, NamingException
    {
        this.simulator = new DevsSimulator<Double>("WebMM1Queue41Application");
        this.model = new WebMM1Queue41Model(this.simulator);
        Replication<Double> replication = new SingleReplication<Double>("rep1", 0.0, 0.0, 1000.0);
        this.simulator.initialize(this.model, replication);
        this.simulator.scheduleEventAbs(1000.0, this, "terminate", null);
        this.simulator.start();
    }

    /** stop the simulation. */
    public void terminate()
    {
        this.simulator.getLogger().always().info("average queue length = " + this.model.qN.getWeightedSampleMean());
        this.simulator.getLogger().always().info("average queue wait   = " + this.model.dN.getSampleMean());
        this.simulator.getLogger().always().info("average utilization  = " + this.model.uN.getWeightedSampleMean());

        System.exit(0);
    }

    /**
     * @param args String[]; can be left empty
     * @throws SimRuntimeException on error
     * @throws RemoteException on error
     * @throws NamingException on error
     */
    public static void main(final String[] args) throws SimRuntimeException, RemoteException, NamingException
    {
        new WebMM1Queue41Application();
    }

}
