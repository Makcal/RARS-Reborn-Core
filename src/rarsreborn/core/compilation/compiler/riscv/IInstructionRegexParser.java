package rarsreborn.core.compilation.compiler.riscv;

import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.core.instruction.IInstruction;

public interface IInstructionRegexParser<TInstruction extends IInstruction> {
    TInstruction parse(String line) throws CompilationException;

    void attachRegisters(IRegisterFile<?> registers);
}
