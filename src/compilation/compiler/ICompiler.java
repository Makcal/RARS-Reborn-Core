package compilation.compiler;

import exceptions.compilation.CompilationException;
import core.instruction.IInstruction;

import java.util.List;

public interface ICompiler {
    List<IInstruction> compile(String source) throws CompilationException;
}
