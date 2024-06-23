package simulator;

import exceptions.compilation.CompilationException;

public interface ISimulator {
    void compile(String program) throws CompilationException;

    void run();

    void runSteps(int n);
}
