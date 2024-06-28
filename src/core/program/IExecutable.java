package core.program;

import core.instruction.IInstruction;

import java.util.List;

public interface IExecutable {
    List<IDataBlock> getData();

    List<IInstruction> getText();
}
