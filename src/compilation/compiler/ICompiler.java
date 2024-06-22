package compilation.compiler;

import exceptions.compilation.CompilationException;
import assembler.instruction.IInstruction;

import java.util.List;

public interface ICompiler {
    List<IInstruction> compile(String source) throws CompilationException;
}
