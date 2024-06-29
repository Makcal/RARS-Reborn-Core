package compilation.compiler;

import core.program.IObjectFile;
import exceptions.compilation.CompilationException;

public interface ICompiler {
    IObjectFile compile(String source) throws CompilationException;
}
