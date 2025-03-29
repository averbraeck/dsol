package nl.tudelft.simulation.examples.dsol.dess;

import java.rmi.RemoteException;

import nl.tudelft.simulation.dsol.simulators.DessSimulatorInterface;
import nl.tudelft.simulation.dsol.statistics.table.PersistentTableModel;
import nl.tudelft.simulation.dsol.swing.gui.DsolPanel;
import nl.tudelft.simulation.dsol.swing.gui.TablePanel;
import nl.tudelft.simulation.dsol.swing.gui.control.GenericControlPanel;
import nl.tudelft.simulation.dsol.swing.statistics.StatisticsTable;

/**
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class DessPanel extends DsolPanel
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param model the model
     * @param simulator the simulator
     * @throws RemoteException on error
     */
    public DessPanel(final DessModel model, final DessSimulatorInterface<Double> simulator) throws RemoteException
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
