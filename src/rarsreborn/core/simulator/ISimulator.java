package rarsreborn.core.simulator;

import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.linking.LinkingException;

public interface ISimulator {
    void compile(String program) throws CompilationException, LinkingException;

    void run();

    void runSteps(int n);

    void reset();
}
