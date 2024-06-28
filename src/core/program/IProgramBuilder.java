package core.program;

import core.instruction.IInstruction;

import java.util.List;

public interface IProgramBuilder {
    IObjectFile build();

    IProgramBuilder addData(List<IDataBlock> dataBlock);

    IProgramBuilder addInstructions(List<IInstruction> instructions);
}
