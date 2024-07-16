package rarsreborn.core.compilation.linker;

import rarsreborn.core.compilation.decoder.IBufferedDecoder;
import rarsreborn.core.core.instruction.ILinkableInstruction;
import rarsreborn.core.core.memory.ArrayBlockStorage;
import rarsreborn.core.core.memory.IMemory;
import rarsreborn.core.core.memory.MemoryBlock;
import rarsreborn.core.core.memory.MemoryBlockWrapper;
import rarsreborn.core.core.program.*;
import rarsreborn.core.core.program.riscvprogram.RiscVExecutable;
import rarsreborn.core.exceptions.compilation.LabelDuplicateException;
import rarsreborn.core.exceptions.execution.IllegalInstructionException;
import rarsreborn.core.exceptions.linking.LinkingException;
import rarsreborn.core.exceptions.linking.SymbolDuplicateException;
import rarsreborn.core.exceptions.linking.TargetAddressTooLargeException;
import rarsreborn.core.exceptions.memory.MemoryAccessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RiscVLinker implements ILinker {
    protected final IBufferedDecoder decoder;
    protected final long dataSectionStart;
    protected final long textSectionStart;

    public RiscVLinker(IBufferedDecoder decoder, long dataSectionStart, long textSectionStart) {
        this.decoder = decoder;
        this.dataSectionStart = dataSectionStart;
        this.textSectionStart = textSectionStart;
    }

    @Override
    public IExecutable link(IObjectFile... objectFiles) throws LinkingException {
        LinkingContext context = new LinkingContext();

        for (IObjectFile objectFile : objectFiles) {
            addData(objectFile, context);
            addText(objectFile, context);
        }

        byte[] text = listToArray(context.text);
        IMemory memoryWrapper = new MemoryBlockWrapper(new MemoryBlock(0, new ArrayBlockStorage(text)));
        try {
            for (IObjectFile objectFile : objectFiles) {
                for (RelocationRecord relocationRecord : objectFile.getRelocationTable().getRecords()) {
                    long localInstructionAddress = context.textSectionOffsets.get(objectFile)
                        + relocationRecord.offset();
                    ILinkableInstruction instruction = (ILinkableInstruction) decoder.decodeNextInstruction(
                        memoryWrapper,
                        localInstructionAddress
                    ).instruction();
                    try {
                        long pcPosition = textSectionStart
                            + localInstructionAddress
                            - relocationRecord.extraCompensation();
                        instruction.link(
                            pcPosition,
                            context.symbolTable.getSymbol(relocationRecord.symbol()).address()
                        );
                    } catch (TargetAddressTooLargeException e) {
                        throw new TargetAddressTooLargeException(relocationRecord.symbol(), e);
                    }
                    memoryWrapper.writeBytes(localInstructionAddress, instruction.serialize());
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
            dataSectionStart,
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
            textSectionStart,
            (byte) 1
        );
    }

    protected void addSection(
            ArrayList<Byte> target,
            byte[] source,
            ISymbolTable totalTable,
            ISymbolTable sourceSymbolTable,
            SymbolType type,
            long baseAddress,
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
                    new Symbol(symbol.type(), symbol.name(), baseAddress + offset + symbol.address())
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
