package compilation.compiler.regex;

import exceptions.compilation.CompilationException;
import assembler.instruction.IInstruction;
import assembler.register.IRegister;

import java.util.Map;

public interface IInstructionRegexParser<TInstruction extends IInstruction> {
    TInstruction parse(String line) throws CompilationException;

    void attachRegisters(Map<String, IRegister> registerFile);
}
