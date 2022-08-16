package nl.tudelft.simulation.dsol.swing.statistics;

import java.awt.Container;
import java.rmi.RemoteException;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;

import nl.tudelft.simulation.dsol.statistics.table.StatisticsTableModel;
import nl.tudelft.simulation.dsol.swing.Swingable;

/**
 * StatisticsTable.java.
 * <p>
 * Copyright (c) 2020-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 * @author <a href="https://www.transport.citg.tudelft.nl">Wouter Schakel</a>
 */
public class StatisticsTable implements Swingable
{
    /** the statistics table that is represented on the screen. */
    private final StatisticsTableModel table;

    /**
     * Constructor.
     * @param table StatisticsTableModel; the statistics table that is represented on the screen
     */
    public StatisticsTable(final StatisticsTableModel table)
    {
        this.table = table;
    }

    /**
     * represents this statisticsObject as Container.
     * @return Container; the result
     * @throws RemoteException on network failure
     */
    @Override
    public Container getSwingPanel() throws RemoteException
    {
        JTable jTable = new JTable(this.table);
        jTable.setEnabled(false);
        JScrollPane pane = new JScrollPane(jTable);
        pane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        return pane;
    }
}
