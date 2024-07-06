package rarsreborn.core.core.environment.riscv;

import rarsreborn.core.core.environment.IExecutionEnvironment;
import rarsreborn.core.core.environment.IInputDevice;
import rarsreborn.core.core.environment.ISystemCall;
import rarsreborn.core.core.memory.IMemory;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.events.IObservable;
import rarsreborn.core.events.IObserver;
import rarsreborn.core.exceptions.compilation.UnknownRegisterException;
import rarsreborn.core.exceptions.execution.ExecutionException;
import rarsreborn.core.exceptions.execution.UnknownSystemCallException;

import java.util.HashMap;
import java.util.Map;

public class RiscV32ExecutionEnvironment implements IExecutionEnvironment, IObservable {
    protected final Register32File registers;
    protected final Register32 programCounter;
    protected final IMemory memory;
    protected final Map<Integer, ISystemCall> handlers;
    protected final IObservable observableImplementation;
    protected final IInputDevice consoleReader;

    public RiscV32ExecutionEnvironment(
        Register32File registers,
        Register32 programCounter,
        IMemory memory,
        Map<Integer, ISystemCall> handlers,
        IObservable observableImplementation,
        IInputDevice consoleReader
    ) {
        this.registers = registers;
        this.programCounter = programCounter;
        this.memory = memory;
        this.handlers = handlers;
        for (ISystemCall systemCall : handlers.values()) {
            if (systemCall instanceof RiscVSystemCall) {
                ((RiscVSystemCall) systemCall).setExecutionEnvironment(this);
            }
        }
        this.observableImplementation = observableImplementation;
        this.consoleReader = consoleReader;
    }

    public IInputDevice getConsoleReader() {
        return consoleReader;
    }

    @Override
    public void call() throws ExecutionException {
        int number;
        try {
            number = registers.getRegisterByNumber(17).getValue(); // a7
        } catch (UnknownRegisterException e) {
            throw new RuntimeException(e);
        }
        ISystemCall handler = handlers.get(number);
        if (handler == null) {
            throw new UnknownSystemCallException(number);
        }
        handler.call();
    }

    @Override
    public void break_() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public <TEvent> void addObserver(Class<TEvent> eventClass, IObserver<TEvent> observer) {
        observableImplementation.addObserver(eventClass, observer);
    }

    @Override
    public <TEvent> void removeObserver(Class<TEvent> eventClass, IObserver<TEvent> observer) {
        observableImplementation.removeObserver(eventClass, observer);
    }

    @Override
    public <TEvent> void notifyObservers(TEvent event) {
        observableImplementation.notifyObservers(event);
    }

    public static class Builder {
        private Register32File registers;
        private Register32 programCounter;
        private IMemory memory;
        protected IObservable observableImplementation;
        protected IInputDevice consoleReader;
        private final Map<Integer, ISystemCall> handlers = new HashMap<>();

        public Builder setRegisters(Register32File registers) {
            this.registers = registers;
            return this;
        }

        public Builder setProgramCounter(Register32 programCounter) {
            this.programCounter = programCounter;
            return this;
        }

        public Builder setMemory(IMemory memory) {
            this.memory = memory;
            return this;
        }

        public Builder setObservableImplementation(IObservable observableImplementation) {
            this.observableImplementation = observableImplementation;
            return this;
        }

        public Builder setConsoleReader(IInputDevice consoleReader) {
            this.consoleReader = consoleReader;
            return this;
        }

        public Builder addHandler(int number, ISystemCall handler) {
            handlers.put(number, handler);
            return this;
        }

        public Builder addHandler(int number, RiscVSystemCall handler) {
            handler.setRegisters(registers);
            handler.setProgramCounter(programCounter);
            handler.setMemory(memory);
            return this.addHandler(number, (ISystemCall) handler);
        }

        public RiscV32ExecutionEnvironment build() {
            return new RiscV32ExecutionEnvironment(
                registers, programCounter, memory, handlers, observableImplementation, consoleReader
            );
        }
    }
}
