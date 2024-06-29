package compilation.compiler.riscv;

import core.instruction.IInstruction;
import core.register.IRegister;
import core.register.IRegisterFile;
import core.register.Register32;
import exceptions.compilation.UnknownRegisterException;
import exceptions.compilation.WrongNumberOfArgumentsException;
import exceptions.compilation.WrongRegisterTypeException;

import java.util.HashMap;
import java.util.Map;

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
        String[] args = line.split(",");
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
}
