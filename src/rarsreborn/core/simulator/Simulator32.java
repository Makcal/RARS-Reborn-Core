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

import java.util.HashMap;

public class Simulator32 extends SimulatorBase {
    protected final Register32File registerFile;
    protected final Register32 programCounter;
    protected final Memory32 memory;
    protected final RiscV32ExecutionEnvironment executionEnvironment;

    protected long programLength;
    protected byte lastInstructionSize;
    protected long lastPcPosition;

    protected IObserver<MemoryChangeEvent> memoryBackStepperObserver;
    protected final HashMap<Register32, IObserver<Register32ChangeEvent>> registerBackStepperObservers = new HashMap<>();

    public Simulator32(
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

    public Simulator32(
        ICompiler compiler,
        ILinker linker,
        IBufferedDecoder decoder,
        Register32File registerFile,
        Register32 programCounter,
        Memory32 memory,
        RiscV32ExecutionEnvironment executionEnvironment,
        IBackStepper backStepper
    ) {
        super(compiler, linker, decoder, backStepper);
        this.registerFile = registerFile;
        this.memory = memory;
        this.programCounter = programCounter;
        this.executionEnvironment = executionEnvironment;
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

    public RiscV32ExecutionEnvironment getExecutionEnvironment() {
        return executionEnvironment;
    }

    @Override
    public void reset() {
        if (memoryBackStepperObserver != null) {
            memory.removeObserver(MemoryChangeEvent.class, memoryBackStepperObserver);
        }
        registerBackStepperObservers.forEach(
            (register, observer) -> register.removeObserver(Register32ChangeEvent.class, observer)
        );
        registerBackStepperObservers.clear();

        backStepper.reset();
        memory.reset();
        registerFile.reset();
        programCounter.setValue(Memory32.TEXT_SECTION_START);
        try {
            registerFile.getRegisterByName("sp").setValue(Memory32.INITIAL_STACK_POINTER);
        } catch (IllegalRegisterException e) {
            throw new RuntimeException(e);
        }
        loadProgram(executable);

        this.memory.addObserver(
            MemoryChangeEvent.class,
            memoryBackStepperObserver = event -> this.backStepper.addChange(new MemoryChange(this.memory, event))
        );
        IObserver<Register32ChangeEvent> observer = event -> this.backStepper.addChange(new Register32Change(event));
        for (Register32 register : registerFile.getAllRegisters()) {
            register.addObserver(
                Register32ChangeEvent.class,
                observer
            );
            registerBackStepperObservers.put(register, observer);
        }
        programCounter.addObserver(
            Register32ChangeEvent.class,
            observer
        );
        registerBackStepperObservers.put(programCounter, observer);
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
            lastPcPosition = programCounter.getValue();
            return decoded.instruction();
        } catch (MemoryAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void executeOneInstruction() throws ExecutionException {
        super.executeOneInstruction();
        if (lastPcPosition == programCounter.getValue())
            programCounter.setValue(programCounter.getValue() + lastInstructionSize);
    }

    public <TInstruction extends IInstruction> Simulator32 registerHandler(
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
