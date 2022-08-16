package nl.tudelft.simulation.dsol.process;

import nl.tudelft.simulation.dsol.interpreter.process.InterpretableProcess;

/**
 * The specifies
 * <p>
 * copyright (c) 2004-2021 <a href="https://simulation.tudelft.nl/dsol/">Delft University of Technology </a>, the Netherlands.
 * <br>
 * See for project information <a href="https://simulation.tudelft.nl/dsol/"> www.simulation.tudelft.nl/dsol </a> <br>
 * License of use: <a href="http://www.gnu.org/copyleft/gpl.html">General Public License (GPL) </a>, no warranty <br>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs"> Peter Jacobs </a>
 * @since 1.5
 */
public class Exec
{
    /**
     * constructs a new Exec.
     */
    public Exec()
    {
        super();
    }

    /**
     * @param process a process to resume
     */
    private static void elaborate(final InterpretableProcess process)
    {
        process.resumeProcess();
    }

    /**
     * @param args String[]; not used
     */
    public static void main(String[] args)
    {
        // elaborate(new Cow());
        elaborate(new Dog());
    }

}
