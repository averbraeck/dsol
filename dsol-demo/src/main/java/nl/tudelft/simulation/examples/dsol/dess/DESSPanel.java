package nl.tudelft.simulation.examples.dsol.dess;

import java.rmi.RemoteException;

import nl.tudelft.simulation.dsol.simulators.DESSSimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.table.PersistentTableModel;
import nl.tudelft.simulation.dsol.swing.gui.DSOLPanel;
import nl.tudelft.simulation.dsol.swing.gui.TablePanel;
import nl.tudelft.simulation.dsol.swing.gui.control.GenericControlPanel;
import nl.tudelft.simulation.dsol.swing.statistics.StatisticsTable;

/**
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DESSPanel extends DSOLPanel
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param model DESSModel; the model
     * @param simulator DESSSimulatorInterface<Double>; the simulator
     * @throws RemoteException on error
     */
    public DESSPanel(final DESSModel model, final DESSSimulatorInterface<Double> simulator) throws RemoteException
    {
        super(new GenericControlPanel<>(model, simulator));

        // add a chart for the demo.
        TablePanel charts = new TablePanel(2, 1);
        getTabbedPane().addTab("statistics", charts);
        getTabbedPane().setSelectedIndex(0);
        charts.setCell(model.getDistanceChart().getSwingPanel(), 0, 0);
        StatisticsTable distanceTable = new StatisticsTable(new PersistentTableModel(model.getDistancePersistent()));
        charts.setCell(distanceTable.getSwingPanel(), 1, 0);
    }
}
