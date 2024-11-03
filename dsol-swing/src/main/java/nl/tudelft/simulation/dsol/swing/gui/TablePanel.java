package nl.tudelft.simulation.dsol.swing.gui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

/**
 * A simple swing component to show a Swing table consisting of rows and columns.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/docs/latest/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/docs/latest/license.html</a>.
 * </p>
 * @author <a href="http://www.tbm.tudelft.nl/mzhang">Mingxin Zhang </a>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck </a>
 */
public class TablePanel extends JPanel
{
    /** */
    private static final long serialVersionUID = 1L;

    /** */
    private int rows;

    /** */
    private int columns;

    /**
     * Constructor for TablePanel, where the number of rows and columns is chosen by the algorithm. It uses the following number
     * of rows and columns, depending on the cells:
     * 
     * <pre>
     * nr = r x c
     * 1 = 1 x 1
     * 2 = 1 x 2
     * 3, 4 = 2 x 2
     * 5, 6 = 2 x 3
     * 7, 8, 9 = 3 x 3
     * 10, 11, 12 = 3 x 4
     * 13, 14, 15, 16 = 4 x 4
     * 17, 18, 19, 20 = 4 x 5
     * 21, 22, 23, 24 = 4 x 6
     * 25 = 5 x 5
     * 26, 27, 28, 29, 30 = 5 x 6
     * </pre>
     * 
     * @param cells int; number of cells
     */
    public TablePanel(final int cells)
    {
        this(calcCols(cells), calcRows(cells));
    }

    /**
     * Constructor for TablePanel.
     * @param columns int; number of columns (x-direction)
     * @param rows int; number of rows (y-direction)
     */
    public TablePanel(final int columns, final int rows)
    {
        super();
        this.rows = rows;
        this.columns = columns;
        double[][] tableDefinition = {this.refractor(this.columns), this.refractor(this.rows)};
        TableLayout layout = new TableLayout(tableDefinition);
        this.setLayout(layout);
    }

    /**
     * Return a nice looking number of rows for the given number of cells.
     * @param cells int; the number of cells.
     * @return int; a nice looking number of rows for the given number of cells
     */
    private static int calcRows(final int cells)
    {
        if (cells < 3)
            return 1;
        if (cells < 7)
            return 2;
        if (cells < 25)
            return 4;
        if (cells < 31)
            return 5;
        return (int) Math.ceil(Math.sqrt(cells));
    }
    
    /**
     * Return a nice looking number of columns for the given number of cells.
     * @param cells int; the number of cells.
     * @return int; a nice looking number of columns for the given number of cells
     */
    private static int calcCols(final int cells)
    {
        if (cells == 1)
            return 1;
        if (cells < 5)
            return 2;
        if (cells < 10)
            return 3;
        if (cells < 17)
            return 4;
        if (cells < 21)
            return 5;
        if (cells < 25)
            return 6;
        if (cells == 25)
            return 5;
        if (cells < 31)
            return 6;
        return (int) Math.ceil(Math.sqrt(cells));
    }

    /**
     * Set the content of the cell at (col, row).
     * @param container Component; the component to put in the cell
     * @param column int; the column number (x, 0-based)
     * @param row int; the row number (y, 0-based)
     */
    public void setCell(final Component container, final int column, final int row)
    {
        this.add(container, "" + column + "," + row + ",C,C");
    }

    /**
     * Set the content of the cell with the given number, starting at row 1.
     * @param container Component; the component to set
     * @param cell int; the cell number
     */
    public void setCell(final Component container, final int cell)
    {
        int row = cell / this.columns;
        int column = cell % this.columns;
        this.add(container, "" + column + "," + row + ",C,C");
    }

    /**
     * Method refractor.
     * @param value int; the number of cells to be used
     * @return double[] the double factors corresponding to 1/value
     */
    private double[] refractor(final int value)
    {
        double[] result = new double[value];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = 1.0 / value;
            if (result[i] == 1.0)
            {
                result[i] = TableLayoutConstants.FILL;
            }
        }
        return result;
    }

    /**
     * tests the TablePanel.
     * @param args String[]; arguments
     */
    public static void main(final String[] args)
    {
        int columns = 5;
        int rows = 10;
        if (args.length != 2)
        {
            System.out.println("Usage: java nl.tudelft.simulation.dsol.gui.TablePanel [#columns] [#rows]");
        }
        else
        {
            columns = Integer.valueOf(args[0]).intValue();
            rows = Integer.valueOf(args[1]).intValue();
        }
        JFrame app = new JFrame();
        TablePanel content = new TablePanel(columns, rows);
        for (int i = 0; i < columns; i++)
        {
            for (int j = 0; j < rows; j++)
            {
                content.setCell(new JTextField("x=" + i + " y=" + j), i, j);
            }
        }
        app.setSize(200, 100);
        app.setMinimumSize(new Dimension(200, 100));
        app.setContentPane(content);
        app.pack();
        app.setVisible(true);
    }
}
