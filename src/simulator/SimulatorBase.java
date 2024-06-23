package simulator;

import compilation.compiler.ICompiler;
import exceptions.compilation.CompilationException;
import core.instruction.IInstruction;
import core.instruction.IInstructionHandler;
import exceptions.execution.EndOfExecutionException;
import exceptions.execution.ExecutionException;
import exceptions.compilation.UnknownInstructionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SimulatorBase implements ISimulator {
    protected final ICompiler compiler;

    protected final Map<Class<? extends IInstruction>, IInstructionHandler<?>> handlers
            = new HashMap<>();

    protected List<IInstruction> compiledInstructions;

    public SimulatorBase(ICompiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public void compile(String program) throws CompilationException {
        compiledInstructions = compiler.compile(program);
    }

    @Override
    public void run() {
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                executeOneInstruction();
            }
        } catch (EndOfExecutionException ignored) {
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void runSteps(int n) {
        try {
            for (int i = 0; i < n; i++) {
                executeOneInstruction();
            }
        } catch (EndOfExecutionException ignored) {
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract IInstruction getNextInstruction() throws EndOfExecutionException;

    protected void executeOneInstruction() throws ExecutionException {
        executeInstruction(getNextInstruction());
    }

    protected void executeInstruction(IInstruction instruction) {
        // This cast should always work due to the fact that handles are registered exceptionally for the corresponding
        // type of instruction
        //noinspection unchecked
        IInstructionHandler<IInstruction> handler
                = (IInstructionHandler<IInstruction>) handlers.get(instruction.getClass());
        if (handler == null) {
            throw new RuntimeException(new UnknownInstructionException(instruction.getName()));
        }
        handler.handle(instruction);
    }

    protected <TInstruction extends IInstruction> void addHandler(
            Class<TInstruction> instructionClass,
            IInstructionHandler<TInstruction> handler
    ) {
        handlers.put(instructionClass, handler);
    }
}
