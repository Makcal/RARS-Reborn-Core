package simulator;

import compilation.compiler.ICompiler;
import exceptions.execution.EndOfExecutionException;
import assembler.instruction.IInstruction;
import assembler.instruction.IInstructionHandler;
import assembler.instruction.IIntegerRegisterInstruction;
import assembler.register.Register32File;

public class Simulator32 extends SimulatorBase {
    protected final Register32File register32File;
    protected int instructionPointer = 0;

    public Simulator32(ICompiler compiler, Register32File registerFile) {
        super(compiler);
        this.register32File = registerFile;
    }

    @Override
    protected IInstruction getNextInstruction() throws EndOfExecutionException {
        if (instructionPointer >= compiledInstructions.size()) {
            throw new EndOfExecutionException();
        }
        return compiledInstructions.get(instructionPointer++);
    }

    public <TInstruction extends IIntegerRegisterInstruction> void registerHandler(
            Class<TInstruction> instructionClass,
            IInstructionHandler<TInstruction> handler
    ) {
        super.addHandler(instructionClass, handler);
    }
}
