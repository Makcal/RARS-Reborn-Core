package compilation.compiler;

import compilation.compiler.regex.IInstructionRegexParser;
import riscv.instruction.IInstruction;
import riscv.register.IRegister;
import riscv.register.IRegisterFile;

public interface ICompilerBuilder {
    ICompiler build();

    ICompilerBuilder registerRegister(IRegister register);

    ICompilerBuilder registerRegistersFromFile(IRegisterFile<?> registerFile);

    <TInstruction extends IInstruction> ICompilerBuilder registerInstruction(
            String instructionName,
            IInstructionRegexParser<TInstruction> parser
    );
}
