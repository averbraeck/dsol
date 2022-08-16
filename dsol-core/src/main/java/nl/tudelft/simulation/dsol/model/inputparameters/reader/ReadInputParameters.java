package nl.tudelft.simulation.dsol.model.inputparameters.reader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.djunits.value.vdouble.scalar.AbsoluteTemperature;
import org.djunits.value.vdouble.scalar.AbsorbedDose;
import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.AmountOfSubstance;
import org.djunits.value.vdouble.scalar.Angle;
import org.djunits.value.vdouble.scalar.Area;
import org.djunits.value.vdouble.scalar.CatalyticActivity;
import org.djunits.value.vdouble.scalar.Density;
import org.djunits.value.vdouble.scalar.Dimensionless;
import org.djunits.value.vdouble.scalar.Direction;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.ElectricalCapacitance;
import org.djunits.value.vdouble.scalar.ElectricalCharge;
import org.djunits.value.vdouble.scalar.ElectricalConductance;
import org.djunits.value.vdouble.scalar.ElectricalCurrent;
import org.djunits.value.vdouble.scalar.ElectricalInductance;
import org.djunits.value.vdouble.scalar.ElectricalPotential;
import org.djunits.value.vdouble.scalar.ElectricalResistance;
import org.djunits.value.vdouble.scalar.Energy;
import org.djunits.value.vdouble.scalar.EquivalentDose;
import org.djunits.value.vdouble.scalar.FlowMass;
import org.djunits.value.vdouble.scalar.FlowVolume;
import org.djunits.value.vdouble.scalar.Force;
import org.djunits.value.vdouble.scalar.Frequency;
import org.djunits.value.vdouble.scalar.Illuminance;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.LinearDensity;
import org.djunits.value.vdouble.scalar.LuminousFlux;
import org.djunits.value.vdouble.scalar.LuminousIntensity;
import org.djunits.value.vdouble.scalar.MagneticFlux;
import org.djunits.value.vdouble.scalar.MagneticFluxDensity;
import org.djunits.value.vdouble.scalar.Mass;
import org.djunits.value.vdouble.scalar.Position;
import org.djunits.value.vdouble.scalar.Power;
import org.djunits.value.vdouble.scalar.Pressure;
import org.djunits.value.vdouble.scalar.RadioActivity;
import org.djunits.value.vdouble.scalar.SolidAngle;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.scalar.Temperature;
import org.djunits.value.vdouble.scalar.Time;
import org.djunits.value.vdouble.scalar.Torque;
import org.djunits.value.vdouble.scalar.Volume;
import org.djunits.value.vdouble.scalar.base.AbstractDoubleScalar;
import org.djunits.value.vfloat.scalar.FloatAbsoluteTemperature;
import org.djunits.value.vfloat.scalar.FloatAbsorbedDose;
import org.djunits.value.vfloat.scalar.FloatAcceleration;
import org.djunits.value.vfloat.scalar.FloatAmountOfSubstance;
import org.djunits.value.vfloat.scalar.FloatAngle;
import org.djunits.value.vfloat.scalar.FloatArea;
import org.djunits.value.vfloat.scalar.FloatCatalyticActivity;
import org.djunits.value.vfloat.scalar.FloatDensity;
import org.djunits.value.vfloat.scalar.FloatDimensionless;
import org.djunits.value.vfloat.scalar.FloatDirection;
import org.djunits.value.vfloat.scalar.FloatDuration;
import org.djunits.value.vfloat.scalar.FloatElectricalCapacitance;
import org.djunits.value.vfloat.scalar.FloatElectricalCharge;
import org.djunits.value.vfloat.scalar.FloatElectricalConductance;
import org.djunits.value.vfloat.scalar.FloatElectricalCurrent;
import org.djunits.value.vfloat.scalar.FloatElectricalInductance;
import org.djunits.value.vfloat.scalar.FloatElectricalPotential;
import org.djunits.value.vfloat.scalar.FloatElectricalResistance;
import org.djunits.value.vfloat.scalar.FloatEnergy;
import org.djunits.value.vfloat.scalar.FloatEquivalentDose;
import org.djunits.value.vfloat.scalar.FloatFlowMass;
import org.djunits.value.vfloat.scalar.FloatFlowVolume;
import org.djunits.value.vfloat.scalar.FloatForce;
import org.djunits.value.vfloat.scalar.FloatFrequency;
import org.djunits.value.vfloat.scalar.FloatIlluminance;
import org.djunits.value.vfloat.scalar.FloatLength;
import org.djunits.value.vfloat.scalar.FloatLinearDensity;
import org.djunits.value.vfloat.scalar.FloatLuminousFlux;
import org.djunits.value.vfloat.scalar.FloatLuminousIntensity;
import org.djunits.value.vfloat.scalar.FloatMagneticFlux;
import org.djunits.value.vfloat.scalar.FloatMagneticFluxDensity;
import org.djunits.value.vfloat.scalar.FloatMass;
import org.djunits.value.vfloat.scalar.FloatPosition;
import org.djunits.value.vfloat.scalar.FloatPower;
import org.djunits.value.vfloat.scalar.FloatPressure;
import org.djunits.value.vfloat.scalar.FloatRadioActivity;
import org.djunits.value.vfloat.scalar.FloatSolidAngle;
import org.djunits.value.vfloat.scalar.FloatSpeed;
import org.djunits.value.vfloat.scalar.FloatTemperature;
import org.djunits.value.vfloat.scalar.FloatTime;
import org.djunits.value.vfloat.scalar.FloatTorque;
import org.djunits.value.vfloat.scalar.FloatVolume;
import org.djunits.value.vfloat.scalar.base.AbstractFloatScalar;
import org.djutils.io.URLResource;

import nl.tudelft.simulation.dsol.model.inputparameters.InputParameter;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterBoolean;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterDistContinuous;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterDistDiscrete;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterDouble;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterDoubleScalar;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterFloat;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterFloatScalar;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterInteger;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterLong;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMap;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterSelectionList;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterSelectionMap;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterString;
import nl.tudelft.simulation.jstats.distributions.DistBernoulli;
import nl.tudelft.simulation.jstats.distributions.DistBeta;
import nl.tudelft.simulation.jstats.distributions.DistBinomial;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistDiscrete;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteConstant;
import nl.tudelft.simulation.jstats.distributions.DistDiscreteUniform;
import nl.tudelft.simulation.jstats.distributions.DistErlang;
import nl.tudelft.simulation.jstats.distributions.DistExponential;
import nl.tudelft.simulation.jstats.distributions.DistGamma;
import nl.tudelft.simulation.jstats.distributions.DistGeometric;
import nl.tudelft.simulation.jstats.distributions.DistLogNormal;
import nl.tudelft.simulation.jstats.distributions.DistNegBinomial;
import nl.tudelft.simulation.jstats.distributions.DistNormal;
import nl.tudelft.simulation.jstats.distributions.DistNormalTrunc;
import nl.tudelft.simulation.jstats.distributions.DistPearson5;
import nl.tudelft.simulation.jstats.distributions.DistPearson6;
import nl.tudelft.simulation.jstats.distributions.DistPoisson;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.jstats.distributions.DistUniform;
import nl.tudelft.simulation.jstats.distributions.DistWeibull;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * Read InputParameters from a Properties file or from an array of Strings. For the properties file, the format is one parameter
 * per line. Keys for the sub-parameters are indicated with a dot notation. The general format is key=value. The value for an
 * integer value called 'nrServers' in the root category:
 * 
 * <pre>
 * nrServers = 2
 * </pre>
 * 
 * The key for a Duration parameter called 'serviceTime' in the category 'server':<br>
 * 
 * <pre>
 * server.serviceTime = 5.0min
 * </pre>
 * 
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public final class ReadInputParameters
{
    /** */
    private ReadInputParameters()
    {
        // utility class
    }

    /**
     * Read the input parameters from a properties file. Note: the input parameter map given as input will be used to store the
     * values read.
     * @param filename String; the file to read (URLResource is used to resolve the file), with one key=value entry per line
     * @param map InputParameterMap; the map of input parameters to be read
     * @throws InputParameterException when the parameter cannot be found or is given an illegal value
     * @throws FileNotFoundException when the file cannot be found
     * @throws IOException on read error from properties file
     */
    public static void loadfromProperties(final String filename, final InputParameterMap map)
            throws InputParameterException, FileNotFoundException, IOException
    {
        InputStream stream = URLResource.getResourceAsStream(filename);
        if (stream == null)
        {
            throw new FileNotFoundException("Loading InputParameters from properties file; could not find file: " + filename);
        }
        Properties properties = new Properties();
        properties.load(stream);
        stream.close();
        for (Object key : properties.keySet())
        {
            InputParameter<?, ?> parameter = map.get(key.toString());
            if (parameter == null)
            {
                throw new InputParameterException(
                        "Parsing input parameters from properties; the following key does not exist: " + key.toString());
            }
            String value = properties.getProperty(key.toString());
            setParameter(parameter, value);
        }
    }

    /**
     * Read the input parameters from an array of Strings, e.g. the args of a command line execution. Note: the input parameter
     * map given as input will be used to store the values read. Do not use whitespace on either side of the equals sign. Values
     * with spaces can be escaped with double quotes or single quotes. These quotes will be removed. Escaping the first single
     * or double quote can be done with a backslash.
     * @param args String[]; the strings to contain the parameters with one key=value entry per string
     * @param ignore boolean; whether to ignore entries without an 'equals' sign
     * @param map InputParameterMap; the map of input parameters to be read
     * @throws InputParameterException when the parameter cannot be found or is given an illegal value
     */
    public static void loadFromArgs(final String[] args, final boolean ignore, final InputParameterMap map)
            throws InputParameterException
    {
        for (String arg : args)
        {
            if (!arg.contains("="))
            {
                if (ignore)
                {
                    continue;
                }
                else
                {
                    throw new InputParameterException(
                            "Parsing input parameters from arguments; the following arg does not contain an equal sign: "
                                    + arg);
                }
            }
            int pos = arg.indexOf("=");
            if (pos == 0)
            {
                throw new InputParameterException("Parsing input parameters from properties; blank key for entry: " + arg);
            }
            if (pos == arg.length() - 1)
            {
                throw new InputParameterException("Parsing input parameters from properties; blank value for entry: " + arg);
            }
            String key = arg.substring(0, pos);
            String value = arg.substring(pos + 1);
            if (value.startsWith("\\") || value.startsWith("\\'"))
            {
                value = value.substring(1);
            }
            else if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'")))
            {
                value = value.substring(1, value.length() - 1);
            }
            InputParameter<?, ?> parameter = map.get(key.toString());
            if (parameter == null)
            {
                throw new InputParameterException(
                        "Parsing input parameters from properties; the following key does not exist: " + key.toString());
            }
            setParameter(parameter, value);
        }
    }

    /**
     * Read the input parameters from an array of Strings, e.g. the args of a command line execution. Note: the input parameter
     * map given as input will be used to store the values read. This method ignores arguments without an 'equals' sign. Do not
     * use whitespace on either side of the equals sign. Values with spaces can be escaped with double quotes or single quotes.
     * These quotes will be removed. Escaping the first single or double quote can be done with a backslash.
     * @param args String[]; the strings to contain the parameters with one key=value entry per string
     * @param map InputParameterMap; the map of input parameters to be read
     * @throws InputParameterException when the parameter cannot be found or is given an illegal value
     */
    public static void loadFromArgs(final String[] args, final InputParameterMap map) throws InputParameterException
    {
        loadFromArgs(args, true, map);
    }

    /**
     * Set a parameter in the input parameter map based on a String value.
     * @param parameter the parameter to set
     * @param value the String value of the parameter
     * @throws InputParameterException when the parameter value is illegal
     */
    public static void setParameter(final InputParameter<?, ?> parameter, final String value) throws InputParameterException
    {
        if (parameter instanceof InputParameterDouble)
        {
            InputParameterDouble param = (InputParameterDouble) parameter;
            param.setDoubleValue(Double.valueOf(value));
        }
        else if (parameter instanceof InputParameterFloat)
        {
            InputParameterFloat param = (InputParameterFloat) parameter;
            param.setFloatValue(Float.valueOf(value));
        }
        else if (parameter instanceof InputParameterBoolean)
        {
            InputParameterBoolean param = (InputParameterBoolean) parameter;
            param.setBooleanValue(value.toUpperCase().startsWith("T"));
        }
        else if (parameter instanceof InputParameterInteger)
        {
            InputParameterInteger param = (InputParameterInteger) parameter;
            param.setIntValue(Integer.valueOf(value));
        }
        else if (parameter instanceof InputParameterLong)
        {
            InputParameterLong param = (InputParameterLong) parameter;
            param.setLongValue(Long.valueOf(value));
        }
        else if (parameter instanceof InputParameterString)
        {
            InputParameterString param = (InputParameterString) parameter;
            param.setStringValue(value);
        }
        else if (parameter instanceof InputParameterDoubleScalar)
        {
            InputParameterDoubleScalar<?, ?> param = (InputParameterDoubleScalar<?, ?>) parameter;
            parseDoubleScalar(param, value);
        }
        else if (parameter instanceof InputParameterFloatScalar)
        {
            InputParameterFloatScalar<?, ?> param = (InputParameterFloatScalar<?, ?>) parameter;
            parseFloatScalar(param, value);
        }
        else if (parameter instanceof InputParameterSelectionList<?>)
        {
            InputParameterSelectionList<?> param = (InputParameterSelectionList<?>) parameter;
            int index = -1;
            for (int i = 0; i < param.getOptions().size(); i++)
            {
                if (value.equals(param.getOptions().get(i).toString()))
                {
                    index = i;
                    break;
                }
            }
            if (index < 0)
            {
                throw new InputParameterException("Input parameter " + value + " not in list: " + param.getOptions());
            }
            param.setIndex(index);
        }
        else if (parameter instanceof InputParameterDistContinuous)
        {
            InputParameterDistContinuous param = (InputParameterDistContinuous) parameter;
            String[] distArgs = splitDist(value);
            double[] args = parseDoubleArgs(distArgs[1]);
            DistContinuous dist = makeDistContinuous(distArgs[0], args, param.getStream());
            param.setDistValue(dist);
        }
        else if (parameter instanceof InputParameterDistDiscrete)
        {
            InputParameterDistDiscrete param = (InputParameterDistDiscrete) parameter;
            String[] distArgs = splitDist(value);
            double[] args = parseDoubleArgs(distArgs[1]);
            DistDiscrete dist = makeDistDiscrete(distArgs[0], args, param.getStream());
            param.setDistValue(dist);
        }
        else if (parameter instanceof InputParameterSelectionMap<?, ?>)
        {
            throw new InputParameterException("A generic input parameter map cannot be read from a property");
        }
        else
        {
            throw new InputParameterException("Property with type " + parameter.getClass().getSimpleName() + " for key "
                    + parameter.getKey() + " cannot be parsed");
        }
    }

    /**
     * Parse a string into a double scalar.
     * @param param the parameter to set
     * @param value the string value
     * @throws InputParameterException on error parsing the value
     */
    @SuppressWarnings("checkstyle:needbraces")
    private static void parseDoubleScalar(final InputParameterDoubleScalar<?, ?> param, final String value)
            throws InputParameterException
    {
        AbstractDoubleScalar<?, ?> scalar;
        if (param.getDefaultTypedValue() instanceof Dimensionless)
            scalar = Dimensionless.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Acceleration)
            scalar = Acceleration.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof SolidAngle)
            scalar = SolidAngle.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Angle)
            scalar = Angle.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Direction)
            scalar = Direction.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Area)
            scalar = Area.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Density)
            scalar = Density.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof ElectricalCharge)
            scalar = ElectricalCharge.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof ElectricalCurrent)
            scalar = ElectricalCurrent.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof ElectricalPotential)
            scalar = ElectricalPotential.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof ElectricalResistance)
            scalar = ElectricalResistance.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Energy)
            scalar = Energy.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FlowMass)
            scalar = FlowMass.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FlowVolume)
            scalar = FlowVolume.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Force)
            scalar = Force.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Frequency)
            scalar = Frequency.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Length)
            scalar = Length.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Position)
            scalar = Position.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof LinearDensity)
            scalar = LinearDensity.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Mass)
            scalar = Mass.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Power)
            scalar = Power.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Pressure)
            scalar = Pressure.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Speed)
            scalar = Speed.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Temperature)
            scalar = Temperature.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof AbsoluteTemperature)
            scalar = AbsoluteTemperature.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Duration)
            scalar = Duration.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Time)
            scalar = Time.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Torque)
            scalar = Torque.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Volume)
            scalar = Volume.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof AbsorbedDose)
            scalar = AbsorbedDose.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof AmountOfSubstance)
            scalar = AmountOfSubstance.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof CatalyticActivity)
            scalar = CatalyticActivity.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof ElectricalCapacitance)
            scalar = ElectricalCapacitance.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof ElectricalConductance)
            scalar = ElectricalConductance.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof ElectricalInductance)
            scalar = ElectricalInductance.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof EquivalentDose)
            scalar = EquivalentDose.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof Illuminance)
            scalar = Illuminance.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof LuminousFlux)
            scalar = LuminousFlux.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof LuminousIntensity)
            scalar = LuminousIntensity.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof MagneticFluxDensity)
            scalar = MagneticFluxDensity.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof MagneticFlux)
            scalar = MagneticFlux.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof RadioActivity)
            scalar = RadioActivity.valueOf(value);
        else
            throw new InputParameterException("Cannot instantiate AbstractDoubleScalar of param " + param.toString());

        param.getDoubleParameter().setDoubleValue(scalar.getInUnit());
        param.getUnitParameter().setObjectValue(scalar.getDisplayUnit());
        param.setCalculatedValue();
    }

    /**
     * Parse a string into a float scalar.
     * @param param the parameter to set
     * @param value the string value
     * @throws InputParameterException on error parsing the value
     */
    @SuppressWarnings("checkstyle:needbraces")
    private static void parseFloatScalar(final InputParameterFloatScalar<?, ?> param, final String value)
            throws InputParameterException
    {
        AbstractFloatScalar<?, ?> scalar;
        if (param.getDefaultTypedValue() instanceof FloatDimensionless)
            scalar = FloatDimensionless.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatAcceleration)
            scalar = FloatAcceleration.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatSolidAngle)
            scalar = FloatSolidAngle.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatAngle)
            scalar = FloatAngle.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatDirection)
            scalar = FloatDirection.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatArea)
            scalar = FloatArea.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatDensity)
            scalar = FloatDensity.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatElectricalCharge)
            scalar = FloatElectricalCharge.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatElectricalCurrent)
            scalar = FloatElectricalCurrent.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatElectricalPotential)
            scalar = FloatElectricalPotential.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatElectricalResistance)
            scalar = FloatElectricalResistance.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatEnergy)
            scalar = FloatEnergy.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatFlowMass)
            scalar = FloatFlowMass.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatFlowVolume)
            scalar = FloatFlowVolume.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatForce)
            scalar = FloatForce.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatFrequency)
            scalar = FloatFrequency.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatLength)
            scalar = FloatLength.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatPosition)
            scalar = FloatPosition.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatLinearDensity)
            scalar = FloatLinearDensity.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatMass)
            scalar = FloatMass.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatPower)
            scalar = FloatPower.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatPressure)
            scalar = FloatPressure.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatSpeed)
            scalar = FloatSpeed.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatTemperature)
            scalar = FloatTemperature.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatAbsoluteTemperature)
            scalar = FloatAbsoluteTemperature.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatDuration)
            scalar = FloatDuration.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatTime)
            scalar = FloatTime.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatTorque)
            scalar = FloatTorque.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatVolume)
            scalar = FloatVolume.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatAbsorbedDose)
            scalar = FloatAbsorbedDose.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatAmountOfSubstance)
            scalar = FloatAmountOfSubstance.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatCatalyticActivity)
            scalar = FloatCatalyticActivity.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatElectricalCapacitance)
            scalar = FloatElectricalCapacitance.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatElectricalConductance)
            scalar = FloatElectricalConductance.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatElectricalInductance)
            scalar = FloatElectricalInductance.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatEquivalentDose)
            scalar = FloatEquivalentDose.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatIlluminance)
            scalar = FloatIlluminance.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatLuminousFlux)
            scalar = FloatLuminousFlux.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatLuminousIntensity)
            scalar = FloatLuminousIntensity.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatMagneticFluxDensity)
            scalar = FloatMagneticFluxDensity.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatMagneticFlux)
            scalar = FloatMagneticFlux.valueOf(value);
        else if (param.getDefaultTypedValue() instanceof FloatRadioActivity)
            scalar = FloatRadioActivity.valueOf(value);
        else
            throw new InputParameterException("Cannot instantiate AbstractFloatScalar of param " + param.toString());

        param.getFloatParameter().setFloatValue(scalar.getInUnit());
        param.getUnitParameter().setObjectValue(scalar.getDisplayUnit());
        param.setCalculatedValue();
    }

    /**
     * Split a distribution function into distribution and arguments. E.g., Normal(0.0, 1.0) becomes {"Normal", "0.0, 1.0"}.
     * @param s String to parse
     * @return String array with function on the first place, and arguments on the subsequent places
     * @throws InputParameterException when no brackets can be found
     */
    private static String[] splitDist(final String s) throws InputParameterException
    {
        int i1 = s.indexOf('(');
        int i2 = s.lastIndexOf(')');
        if (i1 < 0 || i2 < 0)
        {
            throw new InputParameterException("Distribution expression " + s + " does not contain opening / closing bracket");
        }
        return new String[] {s.substring(0, i1), s.substring(i1 + 1, i2)};
    }

    /**
     * Parse a String with comma-separated values, e.g., <code>10.0, 4, 5.23</code>.
     * @param s the string to parse
     * @return array of double values
     * @throws InputParameterException when one of the values does not contain a number
     */
    private static double[] parseDoubleArgs(final String s) throws InputParameterException
    {
        String[] ss = s.split(",");
        double[] d = new double[ss.length];
        for (int i = 0; i < ss.length; i++)
        {
            try
            {
                d[i] = Double.parseDouble(ss[i].trim());
            }
            catch (NumberFormatException nfe)
            {
                throw new InputParameterException("Distribution parameters " + s + " error in parsing parameter number " + i,
                        nfe);
            }
        }
        return d;
    }

    /**
     * Parse a continuous distribution.
     * @param ds the name of the distribution, e.g. UNIF.
     * @param args the parameters of the distribution, e.g. {1.0, 2.0}
     * @param stream the random stream to use
     * @return the generated distribution.
     * @throws InputParameterException in case distribution unknown or parameter number does not match.
     */
    private static DistContinuous makeDistContinuous(final String ds, final double[] args, final StreamInterface stream)
            throws InputParameterException
    {
        try
        {
            switch (ds.toUpperCase())
            {
                case "CONST":
                case "CONSTANT":
                    return new DistConstant(stream, args[0]);

                case "EXPO":
                case "EXPONENTIAL":
                    return new DistExponential(stream, args[0]);

                case "TRIA":
                case "TRIANGULAR":
                    return new DistTriangular(stream, args[0], args[1], args[2]);

                case "NORM":
                case "NORMAL":
                    return new DistNormal(stream, args[0], args[1]);

                case "NORMTRUNC":
                case "NORMALTRUNCATED":
                    return new DistNormalTrunc(stream, args[0], args[1], args[2], args[3]);

                case "BETA":
                    return new DistBeta(stream, args[0], args[1]);

                case "ERLANG":
                    return new DistErlang(stream, args[0], (int) args[1]);

                case "GAMMA":
                    return new DistGamma(stream, args[0], args[1]);

                case "LOGN":
                case "LOGNORMAL":
                    return new DistLogNormal(stream, args[0], args[1]);

                case "PEARSON5":
                    return new DistPearson5(stream, args[0], args[1]);

                case "PEARSON6":
                    return new DistPearson6(stream, args[0], args[1], args[2]);

                case "UNIF":
                case "UNIFORM":
                    return new DistUniform(stream, args[0], args[1]);

                case "WEIB":
                case "WEIBULL":
                    return new DistWeibull(stream, args[0], args[1]);

                default:
                    throw new InputParameterException("makeDistContinuous - unknown distribution function " + ds);
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            throw new InputParameterException(
                    "makeDistContinuous - wrong number of parameters for distribution function " + ds);
        }
    }

    /**
     * Parse a discrete distribution.
     * @param ds the name of the distribution, e.g. UNIF.
     * @param args the parameters of the distribution, e.g. {1.0, 2.0}
     * @param stream the random stream to use
     * @return the generated distribution.
     * @throws InputParameterException in case distribution unknown or parameter number does not match.
     */
    private static DistDiscrete makeDistDiscrete(final String ds, final double[] args, final StreamInterface stream)
            throws InputParameterException
    {
        try
        {
            switch (ds.toUpperCase())
            {
                case "BERNOULLI":
                    return new DistBernoulli(stream, args[0]);

                case "BINOMIAL":
                    return new DistBinomial(stream, (int) args[0], args[1]);

                case "DISCRETECONSTANT":
                    return new DistDiscreteConstant(stream, (long) args[0]);

                case "DISCRETEUNIFORM":
                    return new DistDiscreteUniform(stream, (long) args[0], (long) args[1]);

                case "GEOMETRIC":
                    return new DistGeometric(stream, args[0]);

                case "NEGBINOMIAL":
                    return new DistNegBinomial(stream, (int) args[0], args[1]);

                case "POIS":
                case "POISSON":
                    return new DistPoisson(stream, args[0]);

                default:
                    throw new InputParameterException("makeDistDiscrete - unknown distribution function " + ds);
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            throw new InputParameterException("makeDistDiscrete - wrong number of parameters for distribution function " + ds);
        }
    }
}
