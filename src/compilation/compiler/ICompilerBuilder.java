package compilation.compiler;

import compilation.compiler.riscv.IInstructionRegexParser;
import core.instruction.IInstruction;
import core.register.IRegisterFile;
import core.program.IProgramBuilder;

public interface ICompilerBuilder {
    ICompiler build();

    ICompilerBuilder setProgramBuilder(IProgramBuilder programBuilder);

    ICompilerBuilder registerRegistersFromFile(IRegisterFile<?> registerFile);

    <TInstruction extends IInstruction> ICompilerBuilder registerInstruction(
            String instructionName,
            IInstructionRegexParser<TInstruction> parser
    );
}
