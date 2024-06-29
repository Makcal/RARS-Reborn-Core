package simulator;

import exceptions.compilation.CompilationException;
import exceptions.linking.LinkingException;

public interface ISimulator {
    void compile(String program) throws CompilationException, LinkingException;

    void run();

    void runSteps(int n);
}
