package core.riscvprogram;

import core.instruction.IInstruction;
import core.program.IDataBlock;
import core.program.IExecutable;

import java.util.List;

public class RiscVExecutable implements IExecutable {

    @Override
    public List<IDataBlock> getData() {
        return List.of();
    }

    @Override
    public List<IInstruction> getText() {
        return List.of();
    }
}
