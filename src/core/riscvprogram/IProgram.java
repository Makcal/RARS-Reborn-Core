package core.riscvprogram;

import core.instruction.IInstruction;

import java.util.List;

public interface IProgram {
    List<IDataBlock> getData();

    List<IInstruction> getText();
}
