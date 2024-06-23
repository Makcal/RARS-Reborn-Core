package compilation.compiler.regex;

import exceptions.compilation.CompilationException;
import core.instruction.IInstruction;
import core.register.IRegister;

import java.util.Map;

public interface IInstructionRegexParser<TInstruction extends IInstruction> {
    TInstruction parse(String line) throws CompilationException;

    void attachRegisters(Map<String, IRegister> registerFile);
}
