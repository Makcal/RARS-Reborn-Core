package rarsreborn.core.simulator;

import rarsreborn.core.compilation.compiler.ICompiler;
import rarsreborn.core.compilation.decoder.DecodingResult;
import rarsreborn.core.compilation.decoder.IBufferedDecoder;
import rarsreborn.core.compilation.linker.ILinker;
import rarsreborn.core.core.environment.IExecutionEnvironment;
import rarsreborn.core.core.environment.events.BreakpointEvent;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.IInstructionHandler;
import rarsreborn.core.core.memory.ArrayBlockStorage;
import rarsreborn.core.core.memory.IMemory;
import rarsreborn.core.core.memory.MemoryBlock;
import rarsreborn.core.core.memory.MemoryBlockWrapper;
import rarsreborn.core.core.program.IExecutable;
import rarsreborn.core.core.program.IObjectFile;
import rarsreborn.core.event.IObservable;
import rarsreborn.core.event.IObserver;
import rarsreborn.core.event.ObservableImplementation;
import rarsreborn.core.exceptions.NoBackStepsLeftException;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.execution.EndOfExecutionException;
import rarsreborn.core.exceptions.execution.ExecutionException;
import rarsreborn.core.exceptions.execution.IllegalInstructionException;
import rarsreborn.core.exceptions.linking.LinkingException;
import rarsreborn.core.exceptions.memory.MemoryAccessException;
import rarsreborn.core.simulator.backstepper.BackStepFinishedEvent;
import rarsreborn.core.simulator.backstepper.BackStepperStub;
import rarsreborn.core.simulator.backstepper.IBackStepper;
import rarsreborn.core.simulator.events.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class SimulatorBase implements IMultiFileSimulator, IObservable {
    protected final ICompiler compiler;
    protected final ILinker linker;
    protected final IBufferedDecoder decoder;
    protected final Map<Class<? extends IInstruction>, IInstructionHandler<?>> handlers
            = new HashMap<>();
    protected final IBackStepper backStepper;

    protected final IExecutionEnvironment executionEnvironment;

    protected IExecutable executable;

    protected final Worker worker = new Worker();
    protected final IObservable observableImplementation = new ObservableImplementation();
    protected final IObserver<BreakpointEvent> breakpointObserver = event -> {
        pause();
        notifyObservers(new InstructionExecutedEvent());
    };

    public SimulatorBase(
        ICompiler compiler,
        ILinker linker,
        IBufferedDecoder decoder,
        IBackStepper backStepper,
        IExecutionEnvironment executionEnvironment
    ) {
        this.compiler = compiler;
        this.linker = linker;
        this.decoder = decoder;
        this.backStepper = backStepper == null ? new BackStepperStub() : backStepper;
        this.executionEnvironment = executionEnvironment;
    }

    public IExecutionEnvironment getExecutionEnvironment() {
        return executionEnvironment;
    }

    @Override
    public void compile(String program) throws CompilationException, LinkingException {
        compile(new String[] { program });
    }

    @Override
    public void compile(String ...programs) throws CompilationException, LinkingException {
        IObjectFile[] objectFiles = new IObjectFile[programs.length];
        for (int i = 0; i < programs.length; i++) {
            objectFiles[i] = compiler.compile(programs[i]);
        }
        executable = linker.link(objectFiles);
    }

    @Override
    public List<IInstruction> getProgramInstructions() throws IllegalInstructionException {
        LinkedList<IInstruction> instructions = new LinkedList<>();
        IMemory memoryWrapper
            = new MemoryBlockWrapper(new MemoryBlock(0, new ArrayBlockStorage(executable.getText())));
        try {
            for (long pc = 0; pc + decoder.getBufferSize() <= executable.getText().length;) {
                DecodingResult decodingResult = decoder.decodeNextInstruction(memoryWrapper, pc);
                instructions.add(decodingResult.instruction());
                pc += decodingResult.bytesConsumed();
            }
        } catch (MemoryAccessException e) {
            throw new RuntimeException(e);
        }
        return instructions;
    }

    /**
     * Initializes an execution loop, but does not start execution itself. Better run in a separate thread.
     * @throws ExecutionException any exception during execution
     */
    public void startWorker() throws ExecutionException {
        worker.start();
    }

    public void startWorkerAndRun() throws ExecutionException {
        worker.start(true);
    }

    public boolean isPaused() {
        return worker.isPaused();
    }

    public boolean isRunning() {
        return worker.isRunning();
    }

    /**
     * Starts infinite execution in a worker.
     */
    @Override
    public void run() {
        worker.run();
    }

    /**
     * Execute a limited number of instructions in a worker.
     * @param n the number of steps
     */
    @Override
    public void runSteps(int n) {
        worker.runSteps(n);
    }

    @Override
    public void pause() {
        worker.pause();
    }

    @Override
    public void stop() {
        worker.stop();
    }

    public void stepBack() throws NoBackStepsLeftException, ExecutionException {
        if (!worker.isPaused()) {
            throw new RuntimeException("Wait until the worker is paused");
        }
        worker.stepBack();
        observableImplementation.notifyObservers(new BackStepFinishedEvent());
    }

    abstract protected void loadProgram(IExecutable program);

    protected void onStartSetup() {
        executionEnvironment.removeObserver(BreakpointEvent.class, breakpointObserver);
        executionEnvironment.addObserver(BreakpointEvent.class, breakpointObserver);
        reset();
    }

    protected abstract IInstruction getNextInstruction() throws ExecutionException;

    protected void executeOneInstruction() throws ExecutionException {
        backStepper.addStep();
        notifyObservers(new BeforeInstructionExecutionEvent());
        executeInstruction(getNextInstruction());
        updateProgramCounter();
        notifyObservers(new InstructionExecutedEvent());
    }

    protected abstract void updateProgramCounter();

    public void executeInstruction(IInstruction instruction) throws ExecutionException {
        // This cast should always work due to the fact that handles are registered exceptionally for the corresponding
        // type of instruction
        //noinspection unchecked
        IInstructionHandler<IInstruction> handler
                = (IInstructionHandler<IInstruction>) handlers.get(instruction.getClass());
        if (handler == null) {
            throw new RuntimeException(new IllegalInstructionException(instruction.toString()));
        }
        handler.handle(instruction);
    }

    protected <TInstruction extends IInstruction> void addHandler(
            Class<TInstruction> instructionClass,
            IInstructionHandler<TInstruction> handler
    ) {
        handlers.put(instructionClass, handler);
    }

    @Override
    public <TEvent> void notifyObservers(TEvent event) {
        observableImplementation.notifyObservers(event);
    }

    @Override
    public <TEvent> void removeObserver(Class<TEvent> eventClass, IObserver<TEvent> observer) {
        observableImplementation.removeObserver(eventClass, observer);
    }

    @Override
    public <TEvent> void addObserver(Class<TEvent> eventClass, IObserver<TEvent> observer) {
        observableImplementation.addObserver(eventClass, observer);
    }

    protected class Worker {
        protected final Object lock = new Object();
        protected boolean isRunning = false;
        protected boolean isPaused = true;
        protected int instructionsToRun = 0;
        protected boolean shouldStepBack = false;

        public void start() throws ExecutionException {
            start(false);
        }

        public void start(boolean runImmediately) throws ExecutionException {
            synchronized (lock) {
                if (isRunning) return;
                isRunning = true;
                instructionsToRun = runImmediately ? -1 : 0;
                isPaused = !runImmediately;
            }
            onStartSetup();
            notifyObservers(new StartedEvent());
            while (isRunning()) {
                synchronized (lock) {
                    if (shouldStepBack) {
                        try {
                            backStepper.revert();
                        } catch (NoBackStepsLeftException ignored) {}
                        shouldStepBack = false;
                    }

                    while (isPaused() && isRunning()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException(e);
                        }
                    }
                }

                if (!isRunning())
                    return;

                try {
                    executeOneInstruction();
                    synchronized (lock) {
                        if (instructionsToRun > 0) {
                            instructionsToRun--;
                        }
                        if (instructionsToRun == 0) {
                            pause();
                        }
                    }
                } catch (EndOfExecutionException ignored) {
                    stop();
                }
            }

            stop();
        }

        public void pause() {
            if (!isRunning()) {
                return;
            }
            synchronized (lock) {
                isPaused = true;
                instructionsToRun = 0;
                lock.notify();
            }
            notifyObservers(new PausedEvent());
        }

        public void stop() {
            synchronized (lock) {
                isRunning = false;
                isPaused = true;
                instructionsToRun = 0;
                lock.notify();
            }
            notifyObservers(new StoppedEvent());
        }

        public void run() {
            synchronized (lock) {
                if (!isRunning) throw new RuntimeException("The worker has not been initialized");
                isPaused = false;
                instructionsToRun = -1;
                lock.notify();
            }
        }

        public void runSteps(int n) {
            synchronized (lock) {
                if (!isRunning) throw new RuntimeException("The worker has not been initialized");
                if (instructionsToRun == -1) return;
                isPaused = false;
                instructionsToRun += n;
                lock.notify();
            }
        }

        public boolean isPaused() {
            synchronized (lock) {
                return isPaused || instructionsToRun == 0;
            }
        }

        public boolean isRunning() {
            synchronized (lock) {
                return isRunning;
            }
        }

        public void stepBack() throws NoBackStepsLeftException, ExecutionException {
            if (isPaused()) {
                backStepper.revert();
                return;
            }
            // If requested during waiting for input
            synchronized (lock) {
                shouldStepBack = true;
            }
        }
    }
}
