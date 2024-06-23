package compilation.compiler;

import compilation.compiler.regex.IInstructionRegexParser;
import core.instruction.IInstruction;
import core.register.IRegister;
import core.register.IRegisterFile;

public interface ICompilerBuilder {
    ICompiler build();

    ICompilerBuilder registerRegister(IRegister register);

    ICompilerBuilder registerRegistersFromFile(IRegisterFile<?> registerFile);

    <TInstruction extends IInstruction> ICompilerBuilder registerInstruction(
            String instructionName,
            IInstructionRegexParser<TInstruction> parser
    );
}
