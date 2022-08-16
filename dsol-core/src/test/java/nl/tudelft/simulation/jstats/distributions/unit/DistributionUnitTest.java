package nl.tudelft.simulation.jstats.distributions.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.djunits.unit.AbsoluteTemperatureUnit;
import org.djunits.unit.AbsorbedDoseUnit;
import org.djunits.unit.AccelerationUnit;
import org.djunits.unit.AmountOfSubstanceUnit;
import org.djunits.unit.AngleUnit;
import org.djunits.unit.AreaUnit;
import org.djunits.unit.CatalyticActivityUnit;
import org.djunits.unit.DensityUnit;
import org.djunits.unit.DimensionlessUnit;
import org.djunits.unit.DirectionUnit;
import org.djunits.unit.DurationUnit;
import org.djunits.unit.ElectricalCapacitanceUnit;
import org.djunits.unit.ElectricalChargeUnit;
import org.djunits.unit.ElectricalConductanceUnit;
import org.djunits.unit.ElectricalCurrentUnit;
import org.djunits.unit.ElectricalInductanceUnit;
import org.djunits.unit.ElectricalPotentialUnit;
import org.djunits.unit.ElectricalResistanceUnit;
import org.djunits.unit.EnergyUnit;
import org.djunits.unit.EquivalentDoseUnit;
import org.djunits.unit.FlowMassUnit;
import org.djunits.unit.FlowVolumeUnit;
import org.djunits.unit.ForceUnit;
import org.djunits.unit.FrequencyUnit;
import org.djunits.unit.IlluminanceUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.unit.LinearDensityUnit;
import org.djunits.unit.LuminousFluxUnit;
import org.djunits.unit.LuminousIntensityUnit;
import org.djunits.unit.MagneticFluxDensityUnit;
import org.djunits.unit.MagneticFluxUnit;
import org.djunits.unit.MassUnit;
import org.djunits.unit.PositionUnit;
import org.djunits.unit.PowerUnit;
import org.djunits.unit.PressureUnit;
import org.djunits.unit.RadioActivityUnit;
import org.djunits.unit.SolidAngleUnit;
import org.djunits.unit.SpeedUnit;
import org.djunits.unit.TemperatureUnit;
import org.djunits.unit.TimeUnit;
import org.djunits.unit.TorqueUnit;
import org.djunits.unit.Unit;
import org.djunits.unit.VolumeUnit;
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
import org.djutils.reflection.ClassUtil;
import org.junit.Test;

import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.distributions.DistUniform;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * DistributionUnitTest tests the distributions of scalars with a unit.
 * <p>
 * Copyright (c) 2021-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/dsol/manual/" target="_blank">DSOL Manual</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">DSOL License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DistributionUnitTest
{
    /** unit types to test. */
    @SuppressWarnings("checkstyle:linelength")
    private static final UnitType[] TYPES = {
        // @formatter:off
        new UnitType(AbsoluteTemperature.class, DistContinuousAbsoluteTemperature.class, AbsoluteTemperatureUnit.DEGREE_FAHRENHEIT),
        new UnitType(AbsorbedDose.class, DistContinuousAbsorbedDose.class, AbsorbedDoseUnit.SI),
        new UnitType(Acceleration.class, DistContinuousAcceleration.class, AccelerationUnit.SI),
        new UnitType(AmountOfSubstance.class, DistContinuousAmountOfSubstance.class, AmountOfSubstanceUnit.SI),
        new UnitType(Angle.class, DistContinuousAngle.class, AngleUnit.SI),
        // new UnitType(AngularAcceleration.class, DistContinuousAngularAcceleration.class, AngularAccelerationUnit.ARCMINUTE_PER_SECOND_SQUARED),
        // new UnitType(AngularVelocity.class, DistContinuousAngularVelocity.class, AngularVelocityUnit.GRAD_PER_SECOND),
        new UnitType(Area.class, DistContinuousArea.class, AreaUnit.SI),
        new UnitType(CatalyticActivity.class, DistContinuousCatalyticActivity.class, CatalyticActivityUnit.SI),
        new UnitType(Density.class, DistContinuousDensity.class, DensityUnit.SI),
        new UnitType(Dimensionless.class, DistContinuousDimensionless.class, DimensionlessUnit.SI),
        new UnitType(Direction.class, DistContinuousDirection.class, DirectionUnit.EAST_RADIAN),
        new UnitType(Duration.class, DistContinuousDuration.class, DurationUnit.SI),
        new UnitType(ElectricalCapacitance.class, DistContinuousElectricalCapacitance.class, ElectricalCapacitanceUnit.SI),
        new UnitType(ElectricalCharge.class, DistContinuousElectricalCharge.class, ElectricalChargeUnit.SI),
        new UnitType(ElectricalConductance.class, DistContinuousElectricalConductance.class, ElectricalConductanceUnit.SI),
        new UnitType(ElectricalCurrent.class, DistContinuousElectricalCurrent.class, ElectricalCurrentUnit.SI),
        new UnitType(ElectricalInductance.class, DistContinuousElectricalInductance.class, ElectricalInductanceUnit.SI),
        new UnitType(ElectricalPotential.class, DistContinuousElectricalPotential.class, ElectricalPotentialUnit.SI),
        new UnitType(ElectricalResistance.class, DistContinuousElectricalResistance.class, ElectricalResistanceUnit.SI),
        new UnitType(Energy.class, DistContinuousEnergy.class, EnergyUnit.SI),
        new UnitType(EquivalentDose.class, DistContinuousEquivalentDose.class, EquivalentDoseUnit.SI),
        new UnitType(FlowMass.class, DistContinuousFlowMass.class, FlowMassUnit.SI),
        new UnitType(FlowVolume.class, DistContinuousFlowVolume.class, FlowVolumeUnit.SI),
        new UnitType(Force.class, DistContinuousForce.class, ForceUnit.SI),
        new UnitType(Frequency.class, DistContinuousFrequency.class, FrequencyUnit.SI),
        new UnitType(Illuminance.class, DistContinuousIlluminance.class, IlluminanceUnit.SI),
        new UnitType(Length.class, DistContinuousLength.class, LengthUnit.SI),
        new UnitType(LinearDensity.class, DistContinuousLinearDensity.class, LinearDensityUnit.SI),
        new UnitType(LuminousFlux.class, DistContinuousLuminousFlux.class, LuminousFluxUnit.SI),
        new UnitType(LuminousIntensity.class, DistContinuousLuminousIntensity.class, LuminousIntensityUnit.SI),
        new UnitType(MagneticFlux.class, DistContinuousMagneticFlux.class, MagneticFluxUnit.SI),
        new UnitType(MagneticFluxDensity.class, DistContinuousMagneticFluxDensity.class, MagneticFluxDensityUnit.SI),
        new UnitType(Mass.class, DistContinuousMass.class, MassUnit.SI),
        // new UnitType(Momentum.class, DistContinuousMomentum.class, MomentumUnit.KILOGRAM_METER_PER_SECOND),
        new UnitType(Position.class, DistContinuousPosition.class, PositionUnit.FOOT),
        new UnitType(Power.class, DistContinuousPower.class, PowerUnit.SI),
        new UnitType(Pressure.class, DistContinuousPressure.class, PressureUnit.SI),
        new UnitType(RadioActivity.class, DistContinuousRadioActivity.class, RadioActivityUnit.SI),
        new UnitType(SolidAngle.class, DistContinuousSolidAngle.class, SolidAngleUnit.SI),
        new UnitType(Speed.class, DistContinuousSpeed.class, SpeedUnit.SI),
        new UnitType(Temperature.class, DistContinuousTemperature.class, TemperatureUnit.SI),
        new UnitType(Time.class, DistContinuousTime.class, TimeUnit.DEFAULT),
        new UnitType(Torque.class, DistContinuousTorque.class, TorqueUnit.SI),
        new UnitType(Volume.class, DistContinuousVolume.class, VolumeUnit.SI)
        // @formatter:on
    };

    /**
     * Test whether the distributions with a unit return the right type of sample.
     * @throws SecurityException on error
     * @throws NoSuchMethodException on error
     * @throws InvocationTargetException on error
     * @throws IllegalArgumentException on error
     * @throws IllegalAccessException on error
     * @throws InstantiationException on error
     */
    @Test
    public void testDistributionsUnit() throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        StreamInterface stream = new MersenneTwister(20L);
        DistContinuous dist = new DistUniform(stream, 5.0, 10.0);
        for (UnitType ut : TYPES)
        {
            Class<?> scalarClass = ut.getScalarClass();
            Class<?> distClass = ut.getDistClass();
            Unit<?> unit = ut.getUnit();
            Constructor<?> scalarConstructor = scalarClass.getConstructor(double.class, unit.getClass());
            AbstractDoubleScalar<?, ?> min = (AbstractDoubleScalar<?, ?>) scalarConstructor.newInstance(5.0, unit);
            AbstractDoubleScalar<?, ?> max = (AbstractDoubleScalar<?, ?>) scalarConstructor.newInstance(10.0, unit);
            Constructor<?> distConstructor = distClass.getConstructor(DistContinuous.class, unit.getClass());
            DistContinuousUnit<?, ?> unitDistribution = (DistContinuousUnit<?, ?>) distConstructor.newInstance(dist, unit);
            assertEquals(stream, unitDistribution.getStream());
            assertEquals(unit, unitDistribution.getUnit());
            assertEquals(dist, unitDistribution.getWrappedDistribution());
            for (int i = 0; i < 100; i++)
            {
                AbstractDoubleScalar<?, ?> s = unitDistribution.draw();
                assertNotNull(s);
                assertTrue(s.getClass().isAssignableFrom(scalarClass));
                assertTrue(unitDistribution.toString(), s.getSI() >= min.getSI());
                assertTrue(unitDistribution.toString(), s.getSI() <= max.getSI());
            }
            AbstractDoubleScalar<?, ?> p50 = (AbstractDoubleScalar<?, ?>) scalarConstructor.newInstance(7.5, unit);
            Method densityMethod = ClassUtil.resolveMethod(unitDistribution, "probDensity", new Object[] {p50});
            assertEquals(0.2, (double) densityMethod.invoke(unitDistribution, p50), 0.0001);

            // constructor with SI or Default
            Constructor<?> distConstructorSI = distClass.getConstructor(DistContinuous.class);
            DistContinuousUnit<?, ?> unitDistributionSI = (DistContinuousUnit<?, ?>) distConstructorSI.newInstance(dist);
            assertEquals(stream, unitDistributionSI.getStream());
            assertEquals(unit.getStandardUnit(), unitDistributionSI.getUnit());
            assertEquals(dist, unitDistributionSI.getWrappedDistribution());
        }
    }

    /** Tuple to store expected class info. */
    private static class UnitType
    {
        /** scalar class. */
        private Class<? extends AbstractDoubleScalar<?, ?>> scalarClass;

        /** unit distribution class. */
        private Class<? extends DistContinuousUnit<?, ?>> distClass;

        /** unit to use. */
        private Unit<?> unit;

        /**
         * @param scalarClass scalar class
         * @param distClass unit distribution class
         * @param unit unit to use
         */
        UnitType(final Class<? extends AbstractDoubleScalar<?, ?>> scalarClass,
                final Class<? extends DistContinuousUnit<?, ?>> distClass, final Unit<?> unit)
        {
            this.scalarClass = scalarClass;
            this.distClass = distClass;
            this.unit = unit;
        }

        /**
         * @return scalarClass
         */
        public Class<? extends AbstractDoubleScalar<?, ?>> getScalarClass()
        {
            return this.scalarClass;
        }

        /**
         * @return distClass
         */
        public Class<? extends DistContinuousUnit<?, ?>> getDistClass()
        {
            return this.distClass;
        }

        /**
         * @return unit
         */
        public Unit<?> getUnit()
        {
            return this.unit;
        }
    }

}
