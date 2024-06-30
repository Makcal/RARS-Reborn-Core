package rarsreborn.core.compilation.compiler;

import rarsreborn.core.compilation.compiler.riscv.IInstructionRegexParser;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.program.IProgramBuilder;

public interface ICompilerBuilder {
    ICompiler build();

    ICompilerBuilder setProgramBuilder(IProgramBuilder programBuilder);

    ICompilerBuilder registerRegistersFromFile(IRegisterFile<?> registerFile);

    <TInstruction extends IInstruction> ICompilerBuilder registerInstruction(
            String instructionName,
            IInstructionRegexParser<TInstruction> parser
    );
}
