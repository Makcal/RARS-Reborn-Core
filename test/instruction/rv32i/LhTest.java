package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.RiscVInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Lh;
import rarsreborn.core.core.memory.Memory32;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.compilation.UnknownRegisterException;
import rarsreborn.core.exceptions.memory.MemoryAccessException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LhTest {
    private static final Register32File register32File = new Register32File();
    private static final Memory32 memory = new Memory32();
    private Lh lh;
    private Lh.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2");
    }

    @BeforeEach
    void init() {
        handler = new Lh.Handler();
        handler.attachRegisters(register32File);
        handler.attachMemory(memory);
    }

    @Test
    void lh() throws UnknownRegisterException, MemoryAccessException {
        lh = new Lh(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 0));
        register32File.getRegisterByNumber(1).setValue(Memory32.DATA_SECTION_START);
        memory.setMultiple(Memory32.DATA_SECTION_START, 258, 2);
        handler.handle(lh);
        assertEquals(258, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void lhTruncateLittleEndian() throws UnknownRegisterException, MemoryAccessException {
        lh = new Lh(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 0));
        register32File.getRegisterByNumber(1).setValue(Memory32.DATA_SECTION_START);
        memory.setMultiple(Memory32.DATA_SECTION_START, 259 | (1 << 24), 4);
        handler.handle(lh);
        assertEquals(memory.isLittleEndian() ? 259 : 1 << 8, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void lhNegativeOffset() throws UnknownRegisterException, ImmediateTooLargeException, MemoryAccessException {
        lh = new Lh(new InstructionI.InstructionIParams(
            (byte) 0,
            (byte) 1,
            (short) RiscVInstruction.truncateNegative(-4, 12)
        ));
        register32File.getRegisterByNumber(1).setValue(Memory32.DATA_SECTION_START + 4);
        memory.setMultiple(Memory32.DATA_SECTION_START, 42, 2);
        handler.handle(lh);
        assertEquals(42, register32File.getRegisterByNumber(0).getValue());
    }
}
