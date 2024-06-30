package rarsreborn.core.compilation.compiler;

import rarsreborn.core.core.program.IObjectFile;
import rarsreborn.core.exceptions.compilation.CompilationException;

public interface ICompiler {
    IObjectFile compile(String source) throws CompilationException;
}
