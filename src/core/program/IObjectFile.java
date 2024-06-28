package core.program;

import core.instruction.IInstruction;

import java.util.List;

public interface IObjectFile {
    ISymbolTable getSymbolTable();

    IRelocationTable getRelocationTable();

    List<IDataBlock> getData();

    List<IInstruction> getText();
}
