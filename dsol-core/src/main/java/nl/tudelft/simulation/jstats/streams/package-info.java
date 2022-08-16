/**
 * Provides classes and interfaces for streams used in the JSTATS package. The differences between these generators is expressed
 * by their quality criteria as expressed by (Knuth, 1980):
 * <ul>
 * <li>the computational speed of the algorithm used
 * <li>the period of recurrence
 * </ul>
 * The following table illustrates the streams implemented in the JSTATS package. The speed is computed on a 32-bit Intel based
 * Pentium processor on JDK1.4.2_04 by drawing 10<sup>7</sup> double values from the particular stream.
 * <table border="1">
 * <caption>Speeds of the streams</caption>
 * <tr>
 * <td></td>
 * <td>SPEED (milliseconds)</td>
 * <td>PERIOD</td>
 * </tr>
 * <tr>
 * <td>LC64Generator</td>
 * <td>1292</td>
 * <td>1.8 x 10<sup>19</sup></td>
 * </tr>
 * <tr>
 * <td>Java2Random</td>
 * <td>1452</td>
 * <td>2.8 x 10<sup>14</sup></td>
 * </tr>
 * <tr>
 * <td>DX-120</td>
 * <td>1703</td>
 * <td>0.679 x 10<sup>1120</sup></td>
 * </tr>
 * <tr>
 * <td>MersenneTwister</td>
 * <td>2153</td>
 * <td>1 x 10<sup>6001.6</sup></td>
 * </tr>
 * </table>
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
package nl.tudelft.simulation.jstats.streams;
