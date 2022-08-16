package nl.tudelft.simulation.dsol.interpreter;

import java.io.DataInput;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * copyright (c) 2002-2021 <a href="https://simulation.tudelft.nl">Delft University of Technology</a>. <br>
 * BSD-style license. See <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank"> DSOL License</a>. <br>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs</a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class OpcodeTest
{
    /** Opcodes based on http://en.wikipedia.org/wiki/Java_bytecode_instruction_listings */
    private static final String[][] opcodes = {{"AALOAD", "32", "0"}, {"AASTORE", "53", "0"}, {"ACONST_NULL", "1", "0"},
            {"ALOAD", "19", "1"}, {"ALOAD_0", "2A", "0"}, {"ALOAD_1", "2B", "0"}, {"ALOAD_2", "2C", "0"},
            {"ALOAD_3", "2D", "0"}, {"ANEWARRAY", "BD", "2"}, {"ARETURN", "B0", "0"}, {"ARRAYLENGTH", "BE", "0"},
            {"ASTORE", "3A", "1"}, {"ASTORE_0", "4B", "0"}, {"ASTORE_1", "4C", "0"}, {"ASTORE_2", "4D", "0"},
            {"ASTORE_3", "4E", "0"}, {"ATHROW", "BF", "int"}, {"BALOAD", "33", "0"}, {"BASTORE", "54", "0"},
            {"BIPUSH", "10", "1"}, {"BREAKPOINT", "CA", "0"}, {"CALOAD", "34", "0"}, {"CASTORE", "55", "0"},
            {"CHECKCAST", "C0", "2"}, {"D2F", "90", "0"}, {"D2I", "8E", "0"}, {"D2L", "8F", "0"}, {"DADD", "63", "0"},
            {"DALOAD", "31", "0"}, {"DASTORE", "52", "0"}, {"DCMPG", "98", "0"}, {"DCMPL", "97", "0"}, {"DCONST_0", "0E", "0"},
            {"DCONST_1", "0F", "0"}, {"DDIV", "6F", "0"}, {"DLOAD", "18", "1"}, {"DLOAD_0", "26", "0"}, {"DLOAD_1", "27", "0"},
            {"DLOAD_2", "28", "0"}, {"DLOAD_3", "29", "0"}, {"DMUL", "6B", "0"}, {"DNEG", "77", "0"}, {"DREM", "73", "0"},
            {"DRETURN", "AF", "0"}, {"DSTORE", "39", "1"}, {"DSTORE_0", "47", "0"}, {"DSTORE_1", "48", "0"},
            {"DSTORE_2", "49", "0"}, {"DSTORE_3", "4A", "0"}, {"DSUB", "67", "0"}, {"DUP", "59", "0"}, {"DUP_X1", "5A", "0"},
            {"DUP_X2", "5B", "0"}, {"DUP2", "5C", "0"}, {"DUP2_X1", "5D", "0"}, {"DUP2_X2", "5E", "0"}, {"F2D", "8D", "0"},
            {"F2I", "8B", "0"}, {"F2L", "8C", "0"}, {"FADD", "62", "0"}, {"FALOAD", "30", "0"}, {"FASTORE", "51", "0"},
            {"FCMPG", "96", "0"}, {"FCMPL", "95", "0"}, {"FCONST_0", "0B", "0"}, {"FCONST_1", "0C", "0"},
            {"FCONST_2", "0D", "0"}, {"FDIV", "6E", "0"}, {"FLOAD", "17", "1"}, {"FLOAD_0", "22", "0"}, {"FLOAD_1", "23", "0"},
            {"FLOAD_2", "24", "0"}, {"FLOAD_3", "25", "0"}, {"FMUL", "6A", "0"}, {"FNEG", "76", "0"}, {"FREM", "72", "0"},
            {"FRETURN", "AE", "0"}, {"FSTORE", "38", "1"}, {"FSTORE_0", "43", "0"}, {"FSTORE_1", "44", "0"},
            {"FSTORE_2", "45", "0"}, {"FSTORE_3", "46", "0"}, {"FSUB", "66", "0"}, {"GETFIELD", "B4", "2"},
            {"GETSTATIC", "B2", "2"}, {"GOTO", "A7", "2"}, {"GOTO_W", "C8", "4"}, {"I2B", "91", "0"}, {"I2C", "92", "0"},
            {"I2D", "87", "0"}, {"I2F", "86", "0"}, {"I2L", "85", "0"}, {"I2S", "93", "0"}, {"IADD", "60", "0"},
            {"IALOAD", "2E", "0"}, {"IAND", "7E", "0"}, {"IASTORE", "4F", "0"}, {"ICONST_M1", "2", "0"}, {"ICONST_0", "3", "0"},
            {"ICONST_1", "4", "0"}, {"ICONST_2", "5", "0"}, {"ICONST_3", "6", "0"}, {"ICONST_4", "7", "0"},
            {"ICONST_5", "8", "0"}, {"IDIV", "6C", "0"}, {"IF_ACMPEQ", "A5", "2"}, {"IF_ACMPNE", "A6", "2"},
            {"IF_ICMPEQ", "9F", "2"}, {"IF_ICMPGE", "A2", "2"}, {"IF_ICMPGT", "A3", "2"}, {"IF_ICMPLE", "A4", "2"},
            {"IF_ICMPLT", "A1", "2"}, {"IF_ICMPNE", "A0", "2"}, {"IFEQ", "99", "2"}, {"IFGE", "9C", "2"}, {"IFGT", "9D", "2"},
            {"IFLE", "9E", "2"}, {"IFLT", "9B", "2"}, {"IFNE", "9A", "2"}, {"IFNONNULL", "C7", "2"}, {"IFNULL", "C6", "2"},
            {"IINC", "84", "2"}, {"ILOAD", "15", "1"}, {"ILOAD_0", "1A", "0"}, {"ILOAD_1", "1B", "0"}, {"ILOAD_2", "1C", "0"},
            {"ILOAD_3", "1D", "0"}, {"IMPDEP1", "FE", "0"}, {"IMPDEP2", "FF", "0"}, {"IMUL", "68", "0"}, {"INEG", "74", "0"},
            {"INSTANCEOF", "C1", "2"}, {"INVOKEDYNAMIC", "BA", "4"}, {"INVOKEINTERFACE", "B9", "4"},
            {"INVOKESPECIAL", "B7", "2"}, {"INVOKESTATIC", "B8", "2"}, {"INVOKEVIRTUAL", "B6", "2"}, {"IOR", "80", "0"},
            {"IREM", "70", "0"}, {"IRETURN", "AC", "0"}, {"ISHL", "78", "0"}, {"ISHR", "7A", "0"}, {"ISTORE", "36", "1"},
            {"ISTORE_0", "3B", "0"}, {"ISTORE_1", "3C", "0"}, {"ISTORE_2", "3D", "0"}, {"ISTORE_3", "3E", "0"},
            {"ISUB", "64", "0"}, {"IUSHR", "7C", "0"}, {"IXOR", "82", "0"}, {"JSR", "A8", "d2"}, {"JSR_W", "C9", "4"},
            {"L2D", "8A", "0"}, {"L2F", "89", "0"}, {"L2I", "88", "0"}, {"LADD", "61", "0"}, {"LALOAD", "2F", "0"},
            {"LAND", "7F", "0"}, {"LASTORE", "50", "0"}, {"LCMP", "94", "0"}, {"LCONST_0", "9", "0"}, {"LCONST_1", "0A", "0"},
            {"LDC", "12", "1"}, {"LDC_W", "13", "2"}, {"LDC2_W", "14", "2"}, {"LDIV", "6D", "0"}, {"LLOAD", "16", "1"},
            {"LLOAD_0", "1E", "0"}, {"LLOAD_1", "1F", "0"}, {"LLOAD_2", "20", "0"}, {"LLOAD_3", "21", "0"}, {"LMUL", "69", "0"},
            {"LNEG", "75", "0"}, {"LOOKUPSWITCH", "AB", "lookupswitch"}, {"LOR", "81", "0"}, {"LREM", "71", "0"},
            {"LRETURN", "AD", "0"}, {"LSHL", "79", "0"}, {"LSHR", "7B", "0"}, {"LSTORE", "37", "1"}, {"LSTORE_0", "3F", "0"},
            {"LSTORE_1", "40", "0"}, {"LSTORE_2", "41", "0"}, {"LSTORE_3", "42", "0"}, {"LSUB", "65", "0"},
            {"LUSHR", "7D", "0"}, {"LXOR", "83", "0"}, {"MONITORENTER", "C2", "0"}, {"MONITOREXIT", "C3", "0"},
            {"MULTIANEWARRAY", "C5", "3"}, {"NEW", "BB", "2"}, {"NEWARRAY", "BC", "1"}, {"NOP", "0", "0"}, {"POP", "57", "0"},
            {"POP2", "58", "0"}, {"PUTFIELD", "B5", "2"}, {"PUTSTATIC", "B3", "2"}, {"RET", "A9", "1"}, {"RETURN", "B1", "0"},
            {"SALOAD", "35", "0"}, {"SASTORE", "56", "0"}, {"SIPUSH", "11", "2"}, {"SWAP", "5F", "0"},
            {"TABLESWITCH", "AA", "tableswitch"}, {"WIDE", "C4", "wide"}};

    /**
     * Test the opcodes of all statements based on an official list.
     */
    @Test
    public void testOpcodes()
    {
        for (String[] pair : opcodes)
        {
            String code = pair[0];
            String hexOpcode = pair[1];
            String args = pair[2];

            if (!code.equals("BREAKPOINT") && !code.equals("IMPDEP1") && !code.equals("IMPDEP2"))
            {
                try
                {
                    @SuppressWarnings("unchecked")
                    Class<Operation> clazz =
                            (Class<Operation>) Class.forName("nl.tudelft.simulation.dsol.interpreter.operations." + code);
                    DataInput dataInput = new com.sun.org.apache.bcel.internal.util.ByteSequence(
                            new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                    try
                    {
                        Operation operation = null;
                        if (args.equals("wide"))
                        {
                            DataInput dataInputWide = new com.sun.org.apache.bcel.internal.util.ByteSequence(
                                    new byte[] {0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                            Constructor<Operation> constructor = clazz.getConstructor(new Class<?>[] {DataInput.class});
                            try
                            {
                                operation = constructor.newInstance(dataInputWide);
                            }
                            catch (InvocationTargetException exception)
                            {
                                Assert.fail("OpcodeTest constructor for opcode " + code + ": invocation target exception");
                            }
                            catch (InstantiationException exception)
                            {
                                Assert.fail("OpcodeTest constructor for opcode " + code + ": instantiation exception");
                            }
                            catch (IllegalAccessException exception)
                            {
                                Assert.fail("OpcodeTest constructor for opcode " + code + ": illegal access exception");
                            }
                            int tabBL = 5; // TODO SEEMS INCORRECT
                            int calcBL = operation.getByteLength();
                            Assert.assertEquals("Operation " + code + ": byte length=" + calcBL + ", tab byte length=" + tabBL,
                                    calcBL, tabBL);
                        }

                        else

                        if (code.startsWith("JSR") || args.equals("lookupswitch") || args.equals("tableswitch"))
                        {
                            clazz.getDeclaredMethod("getOpcode", new Class<?>[] {});
                            Constructor<Operation> constructor =
                                    clazz.getConstructor(new Class<?>[] {DataInput.class, int.class});
                            Integer i = Integer.valueOf(1);
                            try
                            {
                                operation = constructor.newInstance(dataInput, i);
                            }
                            catch (InvocationTargetException exception)
                            {
                                Assert.fail("OpcodeTest constructor for opcode " + code + ": invocation target exception");
                            }
                            catch (InstantiationException exception)
                            {
                                Assert.fail("OpcodeTest constructor for opcode " + code + ": instantiation exception");
                            }
                            catch (IllegalAccessException exception)
                            {
                                Assert.fail("OpcodeTest constructor for opcode " + code + ": illegal access exception");
                            }
                            int tabBL = 1;
                            if (code.equals("JSR"))
                                tabBL = 3; // TODO SEEMS INCORRECT
                            else if (code.equals("JSR_W"))
                                tabBL = 5; // TODO SEEMS INCORRECT
                            else if (args.equals("lookupswitch"))
                                tabBL = 10;
                            else if (args.equals("tableswitch"))
                                tabBL = 18;
                            int calcBL = operation.getByteLength();
                            Assert.assertEquals("Operation " + code + ": byte length=" + calcBL + ", tab byte length=" + tabBL,
                                    calcBL, tabBL);
                        }

                        else

                        if (args.equals("int"))
                        {
                            clazz.getDeclaredMethod("getOpcode", new Class<?>[] {});
                            Constructor<Operation> constructor = clazz.getConstructor(new Class<?>[] {int.class});
                            Integer i = Integer.valueOf(1);
                            try
                            {
                                operation = constructor.newInstance(i);
                            }
                            catch (InvocationTargetException exception)
                            {
                                Assert.fail("OpcodeTest constructor for opcode " + code + ": invocation target exception");
                            }
                            catch (InstantiationException exception)
                            {
                                Assert.fail("OpcodeTest constructor for opcode " + code + ": instantiation exception");
                            }
                            catch (IllegalAccessException exception)
                            {
                                Assert.fail("OpcodeTest constructor for opcode " + code + ": illegal access exception");
                            }
                            int tabBL = 1;
                            int calcBL = operation.getByteLength();
                            Assert.assertEquals("Operation " + code + ": byte length=" + calcBL + ", tab byte length=" + tabBL,
                                    calcBL, tabBL);
                        }

                        else

                        {
                            int nr = Integer.parseInt(args);
                            clazz.getMethod("getOpcode", new Class<?>[] {});
                            try
                            {
                                if (nr == 0)
                                    operation = clazz.getDeclaredConstructor().newInstance();
                                else
                                {
                                    Constructor<Operation> constructor = clazz.getConstructor(new Class<?>[] {DataInput.class});
                                    try
                                    {
                                        operation = constructor.newInstance(dataInput);
                                    }
                                    catch (IllegalArgumentException exception)
                                    {
                                        Assert.fail(
                                                "OpcodeTest constructor for opcode " + code + ": illegal argument exception");
                                    }
                                    catch (InvocationTargetException exception)
                                    {
                                        Assert.fail(
                                                "OpcodeTest constructor for opcode " + code + ": invocation target exception");
                                    }
                                }
                                int tabBL = nr + 1;
                                int calcBL = operation.getByteLength();
                                Assert.assertEquals(
                                        "Operation " + code + ": byte length=" + calcBL + ", tab byte length=" + tabBL, calcBL,
                                        tabBL);
                            }
                            catch (InstantiationException exception)
                            {
                                Assert.fail("OpcodeTest constructor for opcode " + code + " failed");
                            }
                            catch (IllegalAccessException exception)
                            {
                                Assert.fail("OpcodeTest constructor for opcode " + code + " not public");
                            }
                            catch (IllegalArgumentException exception)
                            {
                                Assert.fail("OpcodeTest constructor for opcode " + code + " constructor failed");
                            }
                            catch (InvocationTargetException exception)
                            {
                                Assert.fail("OpcodeTest constructor for opcode " + code + " constructor failed");
                            }
                        }
                        int calcOpcode = operation.getOpcode();
                        int tabOpcode = Integer.parseInt(hexOpcode, 16);
                        Assert.assertEquals("Operation " + code + ": calcOpcode=" + calcOpcode + ", tabOpcode=" + tabOpcode,
                                calcOpcode, tabOpcode);
                    }
                    catch (NoSuchMethodException exception)
                    {
                        Assert.fail("OpcodeTest method getOpcode() for opcode " + code + " not found");
                    }
                    catch (SecurityException exception)
                    {
                        Assert.fail("OpcodeTest method getOpcode() for opcode " + code + " not public");
                    }
                }
                catch (ClassNotFoundException exception)
                {
                    Assert.fail("OpcodeTest class for opcode " + code + " not found");
                }
            }
        }
    }
}
