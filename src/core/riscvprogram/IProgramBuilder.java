package core.riscvprogram;

import core.instruction.IInstruction;

import java.util.List;

public interface IProgramBuilder {
    IProgram build();

    IProgramBuilder addData(List<IDataBlock> dataBlock);

    IProgramBuilder addInstructions(List<IInstruction> instructions);
}
