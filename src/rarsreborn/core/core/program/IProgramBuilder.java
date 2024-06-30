package rarsreborn.core.core.program;

public interface IProgramBuilder {
    IObjectFile build();

    IProgramBuilder addData(byte[] dataBlock);

    IProgramBuilder addInstructions(byte[] instructions);

    IProgramBuilder addSymbolTable(ISymbolTable symbolTable);

    IProgramBuilder addRelocationTable(IRelocationTable relocationTable);
}
