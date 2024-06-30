package core.riscvprogram;

import core.program.*;

@SuppressWarnings("ClassCanBeRecord")
public class RiscVObjectFile implements IObjectFile {
    private final ISymbolTable symbolTable;
    private final IRelocationTable relocationTable;
    private final byte[] data;
    private final byte[] text;

    public RiscVObjectFile(ISymbolTable symbolTable, IRelocationTable relocationTable, byte[] data, byte[] text) {
        this.symbolTable = symbolTable;
        this.relocationTable = relocationTable;
        this.data = data;
        this.text = text;
    }

    @Override
    public ISymbolTable getSymbolTable() {
        return symbolTable;
    }

    @Override
    public IRelocationTable getRelocationTable() {
        return relocationTable;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public byte[] getText() {
        return text;
    }

    public static class ProgramBuilder implements IProgramBuilder {
        private ISymbolTable symbolTable;
        private IRelocationTable relocationTable;
        private byte[] dataBlock;
        private byte[] instructions;

        @Override
        public IObjectFile build() {
            return new RiscVObjectFile(symbolTable, relocationTable, dataBlock, instructions);
        }

        @Override
        public IProgramBuilder addData(byte[] dataBlock) {
            this.dataBlock = dataBlock;
            return this;
        }

        @Override
        public IProgramBuilder addInstructions(byte[] instructions) {
            this.instructions = instructions;
            return this;
        }

        @Override
        public IProgramBuilder addSymbolTable(ISymbolTable symbolTable) {
            this.symbolTable = symbolTable;
            return this;
        }

        @Override
        public IProgramBuilder addRelocationTable(IRelocationTable relocationTable) {
            this.relocationTable = relocationTable;
            return this;
        }
    }
}
