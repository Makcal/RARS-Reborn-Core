package rarsreborn.core.compilation.compiler.riscv;

import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.register.IRegister;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.*;

public abstract class InstructionRegexParserRegisterBase
        <TInstruction extends IInstruction>
        implements IInstructionRegexParser<TInstruction> {

    protected IRegisterFile<?> registers;

    @Override
    public void attachRegisters(IRegisterFile<?> registers) {
        this.registers = registers;
    }

    protected static String[] splitArguments(String line, int argumentsCount, String instructionName)
            throws WrongNumberOfArgumentsException {
        String[] args = line.isEmpty() ? new String[]{} : line.split(",");
        if (args.length != argumentsCount) {
            throw new WrongNumberOfArgumentsException(instructionName, args.length, argumentsCount);
        }
        return args;
    }

    protected static IRegister parseRegister(IRegisterFile<?> registers, String s)
            throws UnknownRegisterException {
        IRegister register =
            s.matches("x\\d+")
            ? registers.getRegisterByNumber(Integer.parseInt(s.substring(1)))
            : registers.getRegisterByName(s);
        if (register == null) {
            throw new UnknownRegisterException(s);
        }
        return register;
    }

    protected static Register32 castToRegister32(IRegister register) throws WrongRegisterTypeException {
        try {
            return (Register32) register;
        } catch (ClassCastException e) {
            throw new WrongRegisterTypeException(Register32.class, register.getClass());
        }
    }

    protected static int parseShortMaybeUnsigned(String s) throws CompilationException {
        try {
            long l = Long.parseLong(s);
            if (l < Short.MIN_VALUE || 0xffff < l) {
                throw new ImmediateTooLargeException(l);
            }
            return (int) l;
        } catch (NumberFormatException e) {
            throw new ExpectedIntegerException(s);
        }
    }

    protected static short parseShort(String s) throws CompilationException {
        try {
            long l = Long.parseLong(s);
            if (l < Short.MIN_VALUE || Short.MAX_VALUE < l) {
                throw new ImmediateTooLargeException(l);
            }
            return (short) l;
        } catch (NumberFormatException e) {
            throw new ExpectedIntegerException(s);
        }
    }
}
