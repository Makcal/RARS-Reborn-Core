package compilation.compiler;

import compilation.compiler.regex.IInstructionRegexParser;
import assembler.instruction.IInstruction;
import assembler.register.IRegister;
import assembler.register.IRegisterFile;

public interface ICompilerBuilder {
    ICompiler build();

    ICompilerBuilder registerRegister(IRegister register);

    ICompilerBuilder registerRegistersFromFile(IRegisterFile<?> registerFile);

    <TInstruction extends IInstruction> ICompilerBuilder registerInstruction(
            String instructionName,
            IInstructionRegexParser<TInstruction> parser
    );
}
