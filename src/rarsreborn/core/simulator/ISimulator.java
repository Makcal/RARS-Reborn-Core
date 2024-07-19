package rarsreborn.core.simulator;

import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.execution.ExecutionException;
import rarsreborn.core.exceptions.execution.IllegalInstructionException;
import rarsreborn.core.exceptions.linking.LinkingException;

import java.util.List;

public interface ISimulator {
    void compile(String program) throws CompilationException, LinkingException;

    void reset();

    void run() throws ExecutionException;

    void runSteps(int n) throws ExecutionException;

    void pause();

    void stop();

    List<IInstruction> getProgramInstructions() throws IllegalInstructionException;
}
