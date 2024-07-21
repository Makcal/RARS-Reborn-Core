package rarsreborn.core.simulator;

import rarsreborn.core.compilation.compiler.ICompiler;
import rarsreborn.core.compilation.decoder.DecodingResult;
import rarsreborn.core.compilation.decoder.IBufferedDecoder;
import rarsreborn.core.compilation.linker.ILinker;
import rarsreborn.core.core.environment.riscv.RiscV32ExecutionEnvironment;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.memory.Memory32;
import rarsreborn.core.core.memory.MemoryChangeEvent;
import rarsreborn.core.core.program.IExecutable;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.Register32ChangeEvent;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.event.IObserver;
import rarsreborn.core.exceptions.execution.EndOfExecutionException;
import rarsreborn.core.exceptions.execution.ExecutionException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;
import rarsreborn.core.exceptions.memory.MemoryAccessException;
import rarsreborn.core.simulator.backstepper.IBackStepper;
import rarsreborn.core.simulator.backstepper.MemoryChange;
import rarsreborn.core.simulator.backstepper.Register32Change;

public class SimulatorRiscV extends SimulatorBase {
    protected final Register32File registerFile;
    protected final Register32 programCounter;
    protected final Memory32 memory;

    protected long programLength;
    protected byte lastInstructionSize;
    protected boolean wasProgramCounterAffected;

    protected IObserver<MemoryChangeEvent> memoryBackStepperObserver;
    protected IObserver<Register32ChangeEvent> registerObserver;
    protected IObserver<Register32ChangeEvent> programCounterObserver;
    protected IObserver<BeforeInstructionExecutionEvent> beforeInstructionExecutionObserver;

    public SimulatorRiscV(
        ICompiler compiler,
        ILinker linker,
        IBufferedDecoder decoder,
        Register32File registerFile,
        Register32 programCounter,
        Memory32 memory,
        RiscV32ExecutionEnvironment executionEnvironment
    ) {
        this(compiler, linker, decoder, registerFile, programCounter, memory, executionEnvironment, null);
    }

    public SimulatorRiscV(
        ICompiler compiler,
        ILinker linker,
        IBufferedDecoder decoder,
        Register32File registerFile,
        Register32 programCounter,
        Memory32 memory,
        RiscV32ExecutionEnvironment executionEnvironment,
        IBackStepper backStepper
    ) {
        super(compiler, linker, decoder, backStepper, executionEnvironment);
        this.registerFile = registerFile;
        this.memory = memory;
        this.programCounter = programCounter;
    }

    public Register32File getRegisterFile() {
        return registerFile;
    }

    public Memory32 getMemory() {
        return memory;
    }

    public Register32 getProgramCounter() {
        return programCounter;
    }

    public long getCurrentInstructionNumber() {
        return (programCounter.getValue() - Memory32.TEXT_SECTION_START) / 4;
    }

    protected void clearObservers() {
        if (memoryBackStepperObserver != null) {
            memory.removeObserver(MemoryChangeEvent.class, memoryBackStepperObserver);
        }

        registerFile.getAllRegisters().forEach(
            register -> register.removeObserver(Register32ChangeEvent.class, registerObserver)
        );
        programCounter.removeObserver(Register32ChangeEvent.class, registerObserver);

        programCounter.removeObserver(Register32ChangeEvent.class, programCounterObserver);
        this.removeObserver(BeforeInstructionExecutionEvent.class, beforeInstructionExecutionObserver);
    }

    private void setUpObservers() {
        this.memory.addObserver(
            MemoryChangeEvent.class,
            memoryBackStepperObserver = event -> this.backStepper.addChange(new MemoryChange(this.memory, event))
        );

        registerObserver = event -> this.backStepper.addChange(new Register32Change(event));
        registerFile.getAllRegisters().forEach(
            register -> register.addObserver(Register32ChangeEvent.class, registerObserver)
        );
        programCounter.addObserver(Register32ChangeEvent.class, registerObserver);

        programCounter.addObserver(
            Register32ChangeEvent.class,
            programCounterObserver = event -> wasProgramCounterAffected = true
        );
        this.addObserver(
            BeforeInstructionExecutionEvent.class,
            beforeInstructionExecutionObserver = event -> wasProgramCounterAffected = false
        );
    }

    @Override
    public void reset() {
        clearObservers();

        backStepper.reset();
        memory.reset();
        registerFile.reset();
        programCounter.setValue(Memory32.TEXT_SECTION_START + (int) executable.getEntryPointOffset());
        try {
            registerFile.getRegisterByName("sp").setValue(Memory32.INITIAL_STACK_POINTER);
        } catch (IllegalRegisterException e) {
            throw new RuntimeException(e);
        }
        loadProgram(executable);

        setUpObservers();
    }

    @Override
    protected void loadProgram(IExecutable program) {
        memory.writeBytes(Memory32.DATA_SECTION_START, program.getData());

        byte[] programText = program.getText();
        memory.writeBytes(Memory32.TEXT_SECTION_START, programText);
        programLength = programText.length;
    }

    @Override
    protected IInstruction getNextInstruction() throws ExecutionException {
        if (programCounter.getValue() - Memory32.TEXT_SECTION_START >= programLength) {
            throw new EndOfExecutionException();
        }
        try {
            DecodingResult decoded = decoder.decodeNextInstruction(memory, programCounter.getValue());
            lastInstructionSize = decoded.bytesConsumed();
            return decoded.instruction();
        } catch (MemoryAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void updateProgramCounter() {
        if (!wasProgramCounterAffected) {
            programCounter.setValue(programCounter.getValue() + lastInstructionSize);
        }
    }

    public <TInstruction extends IInstruction> SimulatorRiscV registerHandler(
            Class<TInstruction> instructionClass,
            RiscV32InstructionHandler<TInstruction> handler
    ) {
        handler.attachMemory(memory);
        handler.attachProgramCounter(programCounter);
        handler.attachRegisters(registerFile);
        handler.attachExecutionEnvironment(executionEnvironment);
        super.addHandler(instructionClass, handler);
        return this;
    }
}
