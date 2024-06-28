package core.riscvprogram;

import core.instruction.IInstruction;

import java.util.List;

public class RiscVProgram implements IProgram {
    List<IDataBlock> data;
    List<IInstruction> text;

    public RiscVProgram(List<IDataBlock> data, List<IInstruction> text) {
        this.data = data;
        this.text = text;
    }

    @Override
    public List<IDataBlock> getData() {
        return data;
    }

    @Override
    public List<IInstruction> getText() {
        return text;
    }

    public static class Builder implements IProgramBuilder {
        private List<IDataBlock> dataBlock;
        private List<IInstruction> instructions;

        @Override
        public IProgram build() {
            return new RiscVProgram(dataBlock, instructions);
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
