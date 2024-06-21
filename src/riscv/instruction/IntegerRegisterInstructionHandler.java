package riscv.instruction;

public abstract class IntegerRegisterInstructionHandler<TInstruction extends IIntegerRegisterInstruction>
        implements IInstructionHandler<TInstruction> {

    public IntegerRegisterInstructionHandler() {

    }
}
