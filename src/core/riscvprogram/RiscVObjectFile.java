package core.riscvprogram;

import core.program.*;
import core.instruction.IInstruction;

import java.util.List;

public class RiscVObjectFile implements IObjectFile {
    ISymbolTable symbolTable = new SymbolTable();
    IRelocationTable relocationTable = new RelocationTable();
    List<IDataBlock> data;
    List<IInstruction> text;

    public RiscVObjectFile(List<IDataBlock> data, List<IInstruction> text) {
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
    public List<IDataBlock> getData() {
        return data;
    }

    @Override
    public List<IInstruction> getText() {
        return text;
    }

    public static class ProgramBuilder implements IProgramBuilder {
        private List<IDataBlock> dataBlock;
        private List<IInstruction> instructions;

        @Override
        public IObjectFile build() {
            return new RiscVObjectFile(dataBlock, instructions);
        }

        @Override
        public IProgramBuilder addData(List<IDataBlock> dataBlock) {
            this.dataBlock = dataBlock;
            return this;
        }

        @Override
        public IProgramBuilder addInstructions(List<IInstruction> instructions) {
            this.instructions = instructions;
            return this;
        }
    }
}
