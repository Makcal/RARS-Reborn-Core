package rarsreborn.core.simulator;

import rarsreborn.core.compilation.compiler.ICompiler;
import rarsreborn.core.compilation.decoder.IBufferedDecoder;
import rarsreborn.core.compilation.linker.ILinker;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.IInstructionHandler;
import rarsreborn.core.core.program.IExecutable;
import rarsreborn.core.core.program.IObjectFile;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.compilation.UnknownInstructionException;
import rarsreborn.core.exceptions.execution.EndOfExecutionException;
import rarsreborn.core.exceptions.execution.ExecutionException;
import rarsreborn.core.exceptions.linking.LinkingException;

import java.util.HashMap;
import java.util.Map;

public abstract class SimulatorBase implements IMultiFileSimulator {
    protected final ICompiler compiler;
    protected final ILinker linker;
    protected final IBufferedDecoder decoder;

    protected final Map<Class<? extends IInstruction>, IInstructionHandler<?>> handlers
            = new HashMap<>();

    public SimulatorBase(ICompiler compiler, ILinker linker, IBufferedDecoder decoder) {
        this.compiler = compiler;
        this.linker = linker;
        this.decoder = decoder;
    }

    @Override
    public void compile(String program) throws CompilationException, LinkingException {
        reset();
        IObjectFile objectFile = compiler.compile(program);
        IExecutable executable = linker.link(objectFile);
        loadProgram(executable);
    }

    @Override
    public void compile(String ...programs) throws CompilationException, LinkingException {
        reset();
        IObjectFile[] objectFiles = new IObjectFile[programs.length];
        for (int i = 0; i < programs.length; i++) {
            objectFiles[i] = compiler.compile(programs[i]);
        }
        IExecutable executable = linker.link(objectFiles);
        loadProgram(executable);
    }

    abstract protected void loadProgram(IExecutable program);

    protected void onStartSetup() {}

    @Override
    public void run() {
        onStartSetup();
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

    protected void executeInstruction(IInstruction instruction) throws ExecutionException {
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
