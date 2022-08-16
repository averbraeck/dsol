package nl.tudelft.simulation.language.reflection;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.djutils.exceptions.Try;
import org.djutils.exceptions.Try.Execution;
import org.junit.Test;

import nl.tudelft.simulation.language.DSOLException;

/**
 * SerializableTest.java.
 * <p>
 * Copyright (c) 2019-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djunits.org/docs/license.html">DJUNITS License</a>.
 * <p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class SerializableTest
{
    /**
     * Test the SerializableConstructor.
     * @throws SecurityException on error
     * @throws NoSuchMethodException on error
     * @throws IOException on error
     * @throws ClassNotFoundException on error
     */
    @Test
    public void testSerializableConstructor()
            throws NoSuchMethodException, SecurityException, IOException, ClassNotFoundException
    {
        Constructor<?> c1 = TestClass.class.getConstructor(int.class);
        SerializableConstructor sc1 = new SerializableConstructor(c1);
        SerializableConstructor sc2 = new SerializableConstructor(TestClass.class, new Class<?>[] {int.class});
        assertEquals(sc1.deSerialize(), sc2.deSerialize());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(sc1);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        SerializableConstructor sc3 = (SerializableConstructor) ois.readObject();
        assertEquals(sc1.deSerialize(), sc3.deSerialize());
    }

    /**
     * Test the SerializableField.
     * @throws SecurityException on error
     * @throws NoSuchMethodException on error
     * @throws IOException on error
     * @throws ClassNotFoundException on error
     * @throws NoSuchFieldException on error
     */
    @Test
    public void testSerializableField()
            throws NoSuchMethodException, SecurityException, IOException, ClassNotFoundException, NoSuchFieldException
    {
        Field fv1 = TestClass.class.getDeclaredField("value");
        Field fs1 = TestClass.class.getDeclaredField("self");
        SerializableField sfv1 = new SerializableField(fv1);
        SerializableField sfv2 = new SerializableField(TestClass.class, "value");
        SerializableField sfs1 = new SerializableField(fs1);
        SerializableField sfs2 = new SerializableField(TestClass.class, "self");
        assertEquals(sfv1.deSerialize(), sfv2.deSerialize());
        assertEquals(sfs1.deSerialize(), sfs2.deSerialize());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(sfv1);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        SerializableField sfv3 = (SerializableField) ois.readObject();
        assertEquals(sfv1.deSerialize(), sfv3.deSerialize());
    }

    /**
     * Test the SerializableField.
     * @throws SecurityException on error
     * @throws NoSuchMethodException on error
     * @throws IOException on error
     * @throws ClassNotFoundException on error
     * @throws NoSuchMethodException on error
     */
    @Test
    public void testSerializableMethod()
            throws NoSuchMethodException, SecurityException, IOException, ClassNotFoundException, NoSuchMethodException
    {
        Method fv1 = TestClass.class.getDeclaredMethod("getValue");
        Method fs1 = TestClass.class.getDeclaredMethod("setValue", int.class);
        SerializableMethod sfv1 = new SerializableMethod(fv1);
        SerializableMethod sfv2 = new SerializableMethod(TestClass.class, "getValue");
        SerializableMethod sfs1 = new SerializableMethod(fs1);
        SerializableMethod sfs2 = new SerializableMethod(TestClass.class, "setValue", int.class);
        assertEquals(sfv1.deSerialize(), sfv2.deSerialize());
        assertEquals(sfs1.deSerialize(), sfs2.deSerialize());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(sfv1);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        SerializableMethod sfv3 = (SerializableMethod) ois.readObject();
        assertEquals(sfv1.deSerialize(), sfv3.deSerialize());
    }
    
    /**
     * Test the StateSaver class.
     * @throws DSOLException on error
     */
    @Test
    public void testStateSaver() throws DSOLException
    {
        TestClass tc1 = new TestClass(31);
        byte[] state = StateSaver.saveState(tc1);
        TestClass tc2 = new TestClass(1);
        StateSaver.restoreState(tc2, state);
        assertEquals(tc1, tc2);
        
        Try.testFail(new Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                byte[] wrong = new byte[] {1, 2, 3};
                TestClass tc3 = new TestClass(1);
                StateSaver.restoreState(tc3, wrong);
            }
        }, "restoring state from random byte array should have failed");

        Try.testFail(new Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                TestClassNotSerialisable tc4 = new TestClassNotSerialisable();
                StateSaver.saveState(tc4);
            }
        }, "storing state of not-serializable class should have failed");
}

    /** a class to serialize. */
    public static class TestClass implements Serializable
    {
        /** */
        private static final long serialVersionUID = 1L;
        
        /** static self. */
        private static final TestClass TC = new TestClass(0); 

        /** a serializable field. */
        private final TestClass self;

        /** a primitive serializable field. */
        private int value;

        /**
         * serializable Constructor.
         * @param value the value
         */
        public TestClass(final int value)
        {
            this.self = TC;
            this.value = value;
        }

        /**
         * @return self
         */
        public TestClass getSelf()
        {
            return this.self;
        }

        /**
         * @return value
         */
        public int getValue()
        {
            return this.value;
        }

        /**
         * @param value set value
         */
        public void setValue(final int value)
        {
            this.value = value;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.self == null) ? 0 : this.self.hashCode());
            result = prime * result + this.value;
            return result;
        }

        /** {@inheritDoc} */
        @Override
        @SuppressWarnings("checkstyle:needbraces")
        public boolean equals(final Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            TestClass other = (TestClass) obj;
            if (this.self == null)
            {
                if (other.self != null)
                    return false;
            }
            else if (!this.self.equals(other.self))
                return false;
            if (this.value != other.value)
                return false;
            return true;
        }
    }
    
    /** a class to serialize. */
    public static class TestClassNotSerialisable
    {
        //
    }
}
