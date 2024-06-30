package compilation.linker;

import compilation.decoder.IBufferedDecoder;
import core.instruction.ILinkableInstruction;
import core.memory.ArrayBlockStorage;
import core.memory.MemoryBlock;
import core.program.*;
import core.riscvprogram.RiscVExecutable;
import exceptions.compilation.LabelDuplicateException;
import exceptions.execution.IllegalInstructionException;
import exceptions.linking.LinkingException;
import exceptions.linking.SymbolDuplicateException;
import exceptions.memory.MemoryAccessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RiscVLinker implements ILinker {
    protected final IBufferedDecoder decoder;

    public RiscVLinker(IBufferedDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public IExecutable link(IObjectFile... objectFiles) throws LinkingException {
        LinkingContext context = new LinkingContext();

        for (IObjectFile objectFile : objectFiles) {
            addData(objectFile, context);
            addText(objectFile, context);
        }

        byte[] text = listToArray(context.text);
        MemoryBlock memoryWrapper = new MemoryBlock(0, new ArrayBlockStorage(text));
        try {
            for (IObjectFile objectFile : objectFiles) {
                for (RelocationRecord relocationRecord : objectFile.getRelocationTable().getRecords()) {
                    ILinkableInstruction instruction = (ILinkableInstruction) decoder.decodeNextInstruction(
                        memoryWrapper,
                        relocationRecord.offset()
                    ).instruction();
                    instruction.link(context.symbolTable.getSymbol(relocationRecord.symbol()).address());
                    memoryWrapper.writeBytes(relocationRecord.offset(), instruction.serialize());
                }
            }
        } catch (IllegalInstructionException | MemoryAccessException e) {
            throw new RuntimeException(e);
        }
        return new RiscVExecutable(listToArray(context.data), text);
    }

    protected byte[] listToArray(ArrayList<Byte> list) {
        byte[] array = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    protected void addData(IObjectFile objectFile, LinkingContext context) throws SymbolDuplicateException {
        addSection(
            context.data,
            objectFile.getData(),
            context.symbolTable,
            objectFile.getSymbolTable(),
            SymbolType.DATA,
            (byte) 4
        );
    }

    protected void addText(IObjectFile objectFile, LinkingContext context) throws SymbolDuplicateException {
        context.textSectionOffsets.put(objectFile, (long) context.text.size());
        addSection(
            context.text,
            objectFile.getText(),
            context.symbolTable,
            objectFile.getSymbolTable(),
            SymbolType.INSTRUCTION_LABEL,
            (byte) 1
        );
    }

    protected void addSection(
            ArrayList<Byte> target,
            byte[] source,
            ISymbolTable totalTable,
            ISymbolTable sourceSymbolTable,
            SymbolType type,
            byte alignment
    ) throws SymbolDuplicateException {
        long offset = target.size();
        if (offset % alignment != 0)
            offset += alignment - offset % alignment;
        appendArrayToList(target, source);

        try {
            for (Symbol symbol : sourceSymbolTable.getAllSymbols()) {
                if (symbol.type() != type)
                    continue;
                totalTable.addSymbol(
                    new Symbol(symbol.type(), symbol.name(), symbol.address() + offset)
                );
            }
        } catch (LabelDuplicateException e) {
            throw new SymbolDuplicateException(e.getMessage());
        }
    }

    protected void appendArrayToList(ArrayList<Byte> list, byte[] bytes) {
        for (byte b : bytes) {
            list.add(b);
        }
    }

    protected record LinkingContext(
            ISymbolTable symbolTable,
            Map<IObjectFile, Long> textSectionOffsets,
            ArrayList<Byte> data,
            ArrayList<Byte> text
    ) {
        public LinkingContext() {
            this(new SymbolTable(), new HashMap<>(), new ArrayList<>(), new ArrayList<>());
        }
    }
}
