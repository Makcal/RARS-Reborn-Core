package rarsreborn.core.compilation.compiler.riscv;

import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.register.IRegister;
import rarsreborn.core.core.register.IRegisterCollection;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.floatpoint.RegisterFloat64;
import rarsreborn.core.exceptions.compilation.*;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class InstructionRegexParserRegisterBase
        <TInstruction extends IInstruction>
        implements IInstructionRegexParser<TInstruction> {

    protected IRegisterCollection registers;

    @Override
    public void attachRegisters(IRegisterCollection registers) {
        this.registers = registers;
    }

    public static String[] splitArguments(String line, int argumentsCount, String instructionName)
            throws WrongNumberOfArgumentsException, SyntaxErrorException {
        String[] args = line.isEmpty() ? new String[]{} : line.split("(?<!')[, ](?='?)|(?='?)[, ](?!')");
        if (args.length != argumentsCount) {
            throw new WrongNumberOfArgumentsException(instructionName, args.length, argumentsCount);
        }
        return args;
    }

    public static IRegister parseRegister(IRegisterCollection registers, String s)
            throws UnknownRegisterException {
        try {
            return registers.findRegister(s);
        } catch (IllegalRegisterException e) {
            throw new UnknownRegisterException(e.getMessage());
        }
    }

    public static Register32 castToRegister32(IRegister register) throws WrongRegisterTypeException {
        try {
            return (Register32) register;
        } catch (ClassCastException e) {
            throw new WrongRegisterTypeException(Register32.class, register.getClass());
        }
    }

    public static RegisterFloat64 castToRegisterFloat64(IRegister register) throws WrongRegisterTypeException {
        try {
            return (RegisterFloat64) register;
        } catch (ClassCastException e) {
            throw new WrongRegisterTypeException(RegisterFloat64.class, register.getClass());
        }
    }

    public static short parseShort(String s) throws CompilationException {
        try {
            long l = RegexCompiler.parseLongInteger(s);
            if (l < Short.MIN_VALUE || ((long) Short.MAX_VALUE << 1) + 1 < l) {
                throw new ImmediateTooLargeException(l);
            }
            return (short) l;
        } catch (NumberFormatException e) {
            throw new ExpectedIntegerException(s);
        }
    }

    public static int parseInt(String s) throws CompilationException {
        try {
            long l = RegexCompiler.parseLongInteger(s);
            if (l < Integer.MIN_VALUE || ((long) Integer.MAX_VALUE << 1) + 1 < l) {
                throw new ImmediateTooLargeException(l);
            }
            return (int) l;
        } catch (NumberFormatException e) {
            throw new ExpectedIntegerException(s);
        }
    }

    public static long parseLong(String s) throws CompilationException {
        try {
            return RegexCompiler.parseLongInteger(s);
        } catch (NumberFormatException e) {
            throw new ExpectedIntegerException(s);
        }
    }

    public static final Pattern loadStorePattern =
        Pattern.compile(
            "\\s*(\\w+)\\s*,\\s*(-?(?:0x[0-9a-f]+|0[0-7]+|0b[01]+|\\d+))\\s*\\((x\\d+|\\w+)\\)\\s*"
        );

    /**
     * @param valueRegister rd/rs2
     * @param addressRegister rs1 containing a base memory address
     * @param offset extra offset to be added to the base address
     */
    public record LoadStoreFormatArguments(String valueRegister, String addressRegister, long offset) {}

    public static LoadStoreFormatArguments tryParseLoadStoreFormat(String arguments) throws CompilationException {
        Matcher matcher = loadStorePattern.matcher(arguments);
        if (matcher.matches()) {
            return new LoadStoreFormatArguments(
                matcher.group(1),
                matcher.group(3),
                InstructionRegexParserRegisterBase.parseLong(matcher.group(2))
            );
        }
        return null;
    }

    public static LoadStoreFormatArguments parseLoadStoreFormat(String arguments) throws CompilationException {
        LoadStoreFormatArguments result = tryParseLoadStoreFormat(arguments);
        if (result == null) {
            throw new SyntaxErrorException(arguments);
        }
        return result;
    }
}
