package simulator;

import compilation.compiler.ICompiler;
import compilation.decoder.IBufferedDecoder;
import core.instruction.IInstruction;
import core.instruction.IInstructionHandler;
import core.riscvprogram.IProgram;
import exceptions.compilation.CompilationException;
import exceptions.compilation.UnknownInstructionException;
import exceptions.execution.EndOfExecutionException;
import exceptions.execution.ExecutionException;

import java.util.HashMap;
import java.util.Map;

public abstract class SimulatorBase implements ISimulator {
    protected final ICompiler compiler;
    protected final IBufferedDecoder decoder;

    protected final Map<Class<? extends IInstruction>, IInstructionHandler<?>> handlers
            = new HashMap<>();

    public SimulatorBase(ICompiler compiler, IBufferedDecoder decoder) {
        this.compiler = compiler;
        this.decoder = decoder;
    }

    @Override
    public void compile(String program) throws CompilationException {
        loadProgram(compiler.compile(program));
    }

    abstract protected void loadProgram(IProgram program);

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

    protected abstract IInstruction getNextInstruction() throws ExecutionException;

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
