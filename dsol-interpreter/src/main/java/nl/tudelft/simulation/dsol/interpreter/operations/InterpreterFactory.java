package nl.tudelft.simulation.dsol.interpreter.operations;

import java.io.DataInput;
import java.io.IOException;

import nl.tudelft.simulation.dsol.interpreter.Operation;

/**
 * A InterpreterFactory.
 * <p>
 * Copyright (c) 2002-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class InterpreterFactory implements FactoryInterface
{
    /**
     * constructs a new InterpreterFactory.
     */
    public InterpreterFactory()
    {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public Operation readOperation(final DataInput dataInput, final int startBytePosition) throws IOException
    {
        int operand = dataInput.readUnsignedByte();
        return this.readOperation(operand, dataInput, startBytePosition);
    }

    /** {@inheritDoc} */
    @Override
    public Operation readOperation(final int operand, final DataInput dataInput, final int startBytePositon) throws IOException
    {
        switch (operand)
        {
            case AALOAD.OP:
                return new AALOAD();
            case AASTORE.OP:
                return new AASTORE();
            case ACONST_NULL.OP:
                return new ACONST_NULL();
            case ALOAD.OP:
                return new ALOAD(dataInput);
            case ALOAD_0.OP:
                return new ALOAD_0();
            case ALOAD_1.OP:
                return new ALOAD_1();
            case ALOAD_2.OP:
                return new ALOAD_2();
            case ALOAD_3.OP:
                return new ALOAD_3();
            case ANEWARRAY.OP:
                return new ANEWARRAY(dataInput);
            case ARETURN.OP:
                return new ARETURN();
            case ARRAYLENGTH.OP:
                return new ARRAYLENGTH();
            case ASTORE.OP:
                return new ASTORE(dataInput);
            case ASTORE_0.OP:
                return new ASTORE_0();
            case ASTORE_1.OP:
                return new ASTORE_1();
            case ASTORE_2.OP:
                return new ASTORE_2();
            case ASTORE_3.OP:
                return new ASTORE_3();
            case ATHROW.OP:
                return new ATHROW(startBytePositon);
            case BALOAD.OP:
                return new BALOAD();
            case BASTORE.OP:
                return new BASTORE();
            case BIPUSH.OP:
                return new BIPUSH(dataInput);
            case CALOAD.OP:
                return new CALOAD();
            case CASTORE.OP:
                return new CASTORE();
            case CHECKCAST.OP:
                return new CHECKCAST(dataInput);
            case D2F.OP:
                return new D2F();
            case D2I.OP:
                return new D2I();
            case D2L.OP:
                return new D2L();
            case DADD.OP:
                return new DADD();
            case DALOAD.OP:
                return new DALOAD();
            case DASTORE.OP:
                return new DASTORE();
            case DCMPG.OP:
                return new DCMPG();
            case DCMPL.OP:
                return new DCMPL();
            case DCONST_0.OP:
                return new DCONST_0();
            case DCONST_1.OP:
                return new DCONST_1();
            case DDIV.OP:
                return new DDIV();
            case DLOAD.OP:
                return new DLOAD(dataInput);
            case DLOAD_0.OP:
                return new DLOAD_0();
            case DLOAD_1.OP:
                return new DLOAD_1();
            case DLOAD_2.OP:
                return new DLOAD_2();
            case DLOAD_3.OP:
                return new DLOAD_3();
            case DMUL.OP:
                return new DMUL();
            case DNEG.OP:
                return new DNEG();
            case DREM.OP:
                return new DREM();
            case DRETURN.OP:
                return new DRETURN();
            case DSTORE.OP:
                return new DSTORE(dataInput);
            case DSTORE_0.OP:
                return new DSTORE_0();
            case DSTORE_1.OP:
                return new DSTORE_1();
            case DSTORE_2.OP:
                return new DSTORE_2();
            case DSTORE_3.OP:
                return new DSTORE_3();
            case DSUB.OP:
                return new DSUB();
            case DUP.OP:
                return new DUP();
            case DUP_X1.OP:
                return new DUP_X1();
            case DUP_X2.OP:
                return new DUP_X2();
            case DUP2.OP:
                return new DUP2();
            case DUP2_X1.OP:
                return new DUP2_X1();
            case DUP2_X2.OP:
                return new DUP2_X2();
            case F2D.OP:
                return new F2D();
            case F2I.OP:
                return new F2I();
            case F2L.OP:
                return new F2L();
            case FADD.OP:
                return new FADD();
            case FALOAD.OP:
                return new FALOAD();
            case FASTORE.OP:
                return new FASTORE();
            case FCMPG.OP:
                return new FCMPG();
            case FCMPL.OP:
                return new FCMPL();
            case FCONST_0.OP:
                return new FCONST_0();
            case FCONST_1.OP:
                return new FCONST_1();
            case FCONST_2.OP:
                return new FCONST_2();
            case FDIV.OP:
                return new FDIV();
            case FLOAD.OP:
                return new FLOAD(dataInput);
            case FLOAD_0.OP:
                return new FLOAD_0();
            case FLOAD_1.OP:
                return new FLOAD_1();
            case FLOAD_2.OP:
                return new FLOAD_2();
            case FLOAD_3.OP:
                return new FLOAD_3();
            case FMUL.OP:
                return new FMUL();
            case FNEG.OP:
                return new FNEG();
            case FREM.OP:
                return new FREM();
            case FRETURN.OP:
                return new FRETURN();
            case FSTORE.OP:
                return new FSTORE(dataInput);
            case FSTORE_0.OP:
                return new FSTORE_0();
            case FSTORE_1.OP:
                return new FSTORE_1();
            case FSTORE_2.OP:
                return new FSTORE_2();
            case FSTORE_3.OP:
                return new FSTORE_3();
            case FSUB.OP:
                return new FSUB();
            case GETFIELD.OP:
                return new GETFIELD(dataInput);
            case GETSTATIC.OP:
                return new GETSTATIC(dataInput);
            case GOTO.OP:
                return new GOTO(dataInput);
            case GOTO_W.OP:
                return new GOTO_W(dataInput);
            case I2B.OP:
                return new I2B();
            case I2C.OP:
                return new I2C();
            case I2D.OP:
                return new I2D();
            case I2F.OP:
                return new I2F();
            case I2L.OP:
                return new I2L();
            case I2S.OP:
                return new I2S();
            case IADD.OP:
                return new IADD();
            case IALOAD.OP:
                return new IALOAD();
            case IAND.OP:
                return new IAND();
            case IASTORE.OP:
                return new IASTORE();
            case ICONST_M1.OP:
                return new ICONST_M1();
            case ICONST_0.OP:
                return new ICONST_0();
            case ICONST_1.OP:
                return new ICONST_1();
            case ICONST_2.OP:
                return new ICONST_2();
            case ICONST_3.OP:
                return new ICONST_3();
            case ICONST_4.OP:
                return new ICONST_4();
            case ICONST_5.OP:
                return new ICONST_5();
            case IDIV.OP:
                return new IDIV();
            case IF_ACMPEQ.OP:
                return new IF_ACMPEQ(dataInput);
            case IF_ACMPNE.OP:
                return new IF_ACMPNE(dataInput);
            case IF_ICMPEQ.OP:
                return new IF_ICMPEQ(dataInput);
            case IF_ICMPNE.OP:
                return new IF_ICMPNE(dataInput);
            case IF_ICMPGE.OP:
                return new IF_ICMPGE(dataInput);
            case IF_ICMPGT.OP:
                return new IF_ICMPGT(dataInput);
            case IF_ICMPLE.OP:
                return new IF_ICMPLE(dataInput);
            case IF_ICMPLT.OP:
                return new IF_ICMPLT(dataInput);
            case IFEQ.OP:
                return new IFEQ(dataInput);
            case IFGE.OP:
                return new IFGE(dataInput);
            case IFGT.OP:
                return new IFGT(dataInput);
            case IFLE.OP:
                return new IFLE(dataInput);
            case IFLT.OP:
                return new IFLT(dataInput);
            case IFNE.OP:
                return new IFNE(dataInput);
            case IFNONNULL.OP:
                return new IFNONNULL(dataInput);
            case IFNULL.OP:
                return new IFNULL(dataInput);
            case IINC.OP:
                return new IINC(dataInput);
            case ILOAD.OP:
                return new ILOAD(dataInput);
            case ILOAD_0.OP:
                return new ILOAD_0();
            case ILOAD_1.OP:
                return new ILOAD_1();
            case ILOAD_2.OP:
                return new ILOAD_2();
            case ILOAD_3.OP:
                return new ILOAD_3();
            case IMUL.OP:
                return new IMUL();
            case INEG.OP:
                return new INEG();
            case INSTANCEOF.OP:
                return new INSTANCEOF(dataInput);
            case nl.tudelft.simulation.dsol.interpreter.operations.reflection.INVOKEINTERFACE.OP:
                return new INVOKEINTERFACE(dataInput);
            case nl.tudelft.simulation.dsol.interpreter.operations.reflection.INVOKESPECIAL.OP:
                return new INVOKESPECIAL(dataInput);
            case nl.tudelft.simulation.dsol.interpreter.operations.reflection.INVOKESTATIC.OP:
                return new INVOKESTATIC(dataInput);
            case nl.tudelft.simulation.dsol.interpreter.operations.reflection.INVOKEVIRTUAL.OP:
                return new INVOKEVIRTUAL(dataInput);
            case nl.tudelft.simulation.dsol.interpreter.operations.reflection.INVOKEDYNAMIC.OP:
                return new INVOKEDYNAMIC(dataInput);
            case IOR.OP:
                return new IOR();
            case IREM.OP:
                return new IREM();
            case IRETURN.OP:
                return new IRETURN();
            case ISHL.OP:
                return new ISHL();
            case ISHR.OP:
                return new ISHR();
            case ISTORE.OP:
                return new ISTORE(dataInput);
            case ISTORE_0.OP:
                return new ISTORE_0();
            case ISTORE_1.OP:
                return new ISTORE_1();
            case ISTORE_2.OP:
                return new ISTORE_2();
            case ISTORE_3.OP:
                return new ISTORE_3();
            case ISUB.OP:
                return new ISUB();
            case IUSHR.OP:
                return new IUSHR();
            case IXOR.OP:
                return new IXOR();
            case JSR_W.OP:
                return new JSR_W(dataInput, startBytePositon);
            case JSR.OP:
                return new JSR(dataInput, startBytePositon);
            case L2D.OP:
                return new L2D();
            case L2F.OP:
                return new L2F();
            case L2I.OP:
                return new L2I();
            case LADD.OP:
                return new LADD();
            case LALOAD.OP:
                return new LALOAD();
            case LAND.OP:
                return new LAND();
            case LASTORE.OP:
                return new LASTORE();
            case LCMP.OP:
                return new LCMP();
            case LCONST_0.OP:
                return new LCONST_0();
            case LCONST_1.OP:
                return new LCONST_1();
            case LDC_W.OP:
                return new LDC_W(dataInput);
            case LDC.OP:
                return new LDC(dataInput);
            case LDC2_W.OP:
                return new LDC2_W(dataInput);
            case LDIV.OP:
                return new LDIV();
            case LLOAD_0.OP:
                return new LLOAD_0();
            case LLOAD_1.OP:
                return new LLOAD_1();
            case LLOAD_2.OP:
                return new LLOAD_2();
            case LLOAD_3.OP:
                return new LLOAD_3();
            case LLOAD.OP:
                return new LLOAD(dataInput);
            case LMUL.OP:
                return new LMUL();
            case LNEG.OP:
                return new LNEG();
            case LOOKUPSWITCH.OP:
                return new LOOKUPSWITCH(dataInput, (4 - ((startBytePositon + 1) % 4)) % 4);
            case LOR.OP:
                return new LOR();
            case LREM.OP:
                return new LREM();
            case LRETURN.OP:
                return new LRETURN();
            case LSHL.OP:
                return new LSHL();
            case LSHR.OP:
                return new LSHR();
            case LSTORE_0.OP:
                return new LSTORE_0();
            case LSTORE_1.OP:
                return new LSTORE_1();
            case LSTORE_2.OP:
                return new LSTORE_2();
            case LSTORE_3.OP:
                return new LSTORE_3();
            case LSTORE.OP:
                return new LSTORE(dataInput);
            case LSUB.OP:
                return new LSUB();
            case LUSHR.OP:
                return new LUSHR();
            case LXOR.OP:
                return new LXOR();
            case MONITORENTER.OP:
                return new MONITORENTER();
            case MONITOREXIT.OP:
                return new MONITOREXIT();
            case MULTIANEWARRAY.OP:
                return new MULTIANEWARRAY(dataInput);
            case NEW.OP:
                return new NEW(dataInput);
            case NEWARRAY.OP:
                return new NEWARRAY(dataInput);
            case NOP.OP:
                return new NOP();
            case POP.OP:
                return new POP();
            case POP2.OP:
                return new POP2();
            case PUTFIELD.OP:
                return new PUTFIELD(dataInput);
            case PUTSTATIC.OP:
                return new PUTSTATIC(dataInput);
            case RET.OP:
                return new RET(dataInput);
            case RETURN.OP:
                return new RETURN();
            case SALOAD.OP:
                return new SALOAD();
            case SASTORE.OP:
                return new SASTORE();
            case SIPUSH.OP:
                return new SIPUSH(dataInput);
            case SWAP.OP:
                return new SWAP();
            case TABLESWITCH.OP:
                return new TABLESWITCH(dataInput, (4 - ((startBytePositon + 1) % 4)) % 4);
            case WIDE.OP:
                return new WIDE(dataInput);
            case IMPDEP1:
                return new NOP();
            case IMPDEP2:
                return new NOP();
            case BREAKPOINT:
                return new NOP();
            default:
                throw new IOException("Unable to read operation[" + operand + "]");
        }
    }
}
