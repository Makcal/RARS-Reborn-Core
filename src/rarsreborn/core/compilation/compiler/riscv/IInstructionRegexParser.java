package rarsreborn.core.compilation.compiler.riscv;

import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.register.IRegisterCollection;
import rarsreborn.core.exceptions.compilation.CompilationException;

public interface IInstructionRegexParser<TInstruction extends IInstruction> {
    TInstruction parse(String line) throws CompilationException;

    void attachRegisters(IRegisterCollection registers);
}
