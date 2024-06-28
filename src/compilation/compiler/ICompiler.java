package compilation.compiler;

import core.riscvprogram.IProgram;
import exceptions.compilation.CompilationException;

public interface ICompiler {
    IProgram compile(String source) throws CompilationException;
}
