package compilation.compiler.regex;

import riscv.instruction.IInstruction;
import riscv.register.IRegister;

import java.util.HashMap;
import java.util.Map;

public abstract class InstructionRegexParserRegisterBase
        <TInstruction extends IInstruction>
        implements IInstructionRegexParser<TInstruction> {

    protected Map<String, IRegister> registers = new HashMap<>();

    @Override
    public void attachRegisters(Map<String, IRegister> registers) {
        this.registers = registers;
    }
}
