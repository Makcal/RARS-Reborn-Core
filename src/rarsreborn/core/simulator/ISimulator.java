package rarsreborn.core.simulator;

import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.execution.ExecutionException;
import rarsreborn.core.exceptions.linking.LinkingException;

public interface ISimulator {
    void compile(String program) throws CompilationException, LinkingException;

    void reset();

    void run() throws ExecutionException;

    void runSteps(int n) throws ExecutionException;

    void pause();

    void stop();
}
