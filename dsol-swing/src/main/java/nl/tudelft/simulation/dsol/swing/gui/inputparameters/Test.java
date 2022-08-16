package nl.tudelft.simulation.dsol.swing.gui.inputparameters;

import java.util.ArrayList;
import java.util.List;

import org.djunits.unit.LengthUnit;
import org.djunits.value.vdouble.scalar.Length;

import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterBoolean;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterDistContinuousSelection;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterDistDiscreteSelection;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterDouble;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterDoubleScalar;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMap;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterSelectionList;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * Test.java. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Test
{

    /**
     * @param args String[]; empty
     * @throws InputParameterException on error
     */
    public static void main(final String[] args) throws InputParameterException
    {
        InputParameterMap ipMap = new InputParameterMap("model", "Model data", "Input variables for the model", 1.0);

        InputParameterMap seMap = new InputParameterMap("economic", "Socio-economic", "Socio-economic data", 1.0);
        ipMap.add(seMap);
        for (String tgsbf : new String[] {"Textile", "Garment", "Steel", "Brick", "Food"})
        {
            for (String pcei : new String[] {"Production", "Consumption", "Import", "Export"})
            {
                InputParameterDouble paramDouble = new InputParameterDouble(tgsbf + pcei, tgsbf + " " + pcei,
                        tgsbf + " " + pcei + " in tonnes per year", 100000.0, 0.0, 1.0E12, true, true, "%f", 1.0);
                seMap.add(paramDouble);
            }
        }

        InputParameterMap trMap = new InputParameterMap("transport", "Transport", "Transport data", 2.0);
        ipMap.add(trMap);
        for (String tgsbf : new String[] {"Textile", "Garment", "Steel", "Brick", "Food"})
        {
            for (String rrw : new String[] {"Road", "Rail", "Water"})
            {
                InputParameterDouble paramDouble = new InputParameterDouble(tgsbf + rrw, tgsbf + " " + rrw,
                        tgsbf + " " + rrw + " in tonnes per year", 100000.0, 0.0, 1.0E12, true, true, "%f", 1.0);
                trMap.add(paramDouble);
            }
        }

        InputParameterMap xxMap = new InputParameterMap("other", "Other", "Other parameters", 3.0);
        ipMap.add(xxMap);
        InputParameterBoolean paramBool1 =
                new InputParameterBoolean("boolean1", "Boolean value 1", "Boolean value 1 using tickbox false", false, 1.0);
        xxMap.add(paramBool1);
        InputParameterBoolean paramBool2 =
                new InputParameterBoolean("boolean2", "Boolean value 2", "Boolean value 2 using tickbox true", true, 2.0);
        xxMap.add(paramBool2);
        List<String> countries = new ArrayList<>();
        countries.add("USA");
        countries.add("Netherlands");
        countries.add("Germany");
        countries.add("France");
        countries.add("Belgium");
        InputParameterSelectionList<String> paramSelect = new InputParameterSelectionList<String>("country", "Country",
                "Country to select", countries, "Netherlands", 4.0);
        xxMap.add(paramSelect);
        StreamInterface stream = new MersenneTwister(1L);
        InputParameterDistContinuousSelection ipdcs = new InputParameterDistContinuousSelection("distCont",
                "Continuous distribution", "Continuous distribution", stream, 5.0);
        xxMap.add(ipdcs);
        InputParameterDistDiscreteSelection ipdds = new InputParameterDistDiscreteSelection("distDiscrete",
                "Discrete distribution", "Discrete distribution", stream, 6.0);
        xxMap.add(ipdds);
        InputParameterDoubleScalar<LengthUnit, Length> length = new InputParameterDoubleScalar<>("length", "Length",
                "Length of the trip", new Length(20.0, LengthUnit.LIGHTYEAR), 0.0, Double.MAX_VALUE, true, false, "%d", 7.0);
        xxMap.add(length);

        // InputParameterString is = new InputParameterString("runName", "Name of run", "Description of the run", "", 1.0);
        // ipMap.add(is); // should give error when displayed in a tabbed environment

        // InputParameterMap inputParameterMap = ipMap;
        // InputParameterDistContinuous ipDist = new InputParameterDistContinuous("arrDist", "Arrival distribution",
        // "Arrival distribution, e.g. DistExponential(lambda)", stream, new DistExponential(stream, 1.0), 1.0);
        // ipMap.add(ipDist);
        //
        // InputParameterMap ipMap = getInputParameterMap();
        // InputParameterDistContinuous arrDist = (InputParameterDistContinuous) ipMap.get("arrDist");
        // arrDist.setDistValue(new DistExponential(arrDist.getStream(), 2.17));

        new TabbedParameterDialog(ipMap);

        System.out.println(ipMap.printValues());
        System.out.println(((InputParameterMap) ipMap.get("other")).get("length").toString());
        @SuppressWarnings("unchecked")
        InputParameterDoubleScalar<LengthUnit, Length> ipds =
                (InputParameterDoubleScalar<LengthUnit, Length>) ((InputParameterMap) ipMap.get("other")).get("length");
        Length l1 = ipds.getCalculatedValue();
        Length l2 = (Length) ipMap.get("other.length").getCalculatedValue();
        System.out.println(l1);
        System.out.println(l2);
    }

}
