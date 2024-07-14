package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.RiscVInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Lb;
import rarsreborn.core.core.memory.Memory32;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;
import rarsreborn.core.exceptions.memory.MemoryAccessException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LbTest {
    private static final Register32File register32File = new Register32File();
    private static final Memory32 memory = new Memory32();
    private Lb lb;
    private Lb.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2");
    }

    @BeforeEach
    void init() {
        handler = new Lb.Handler();
        handler.attachRegisters(register32File);
        handler.attachMemory(memory);
    }

    @Test
    void lb() throws MemoryAccessException, IllegalRegisterException {
        lb = new Lb(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 0));
        register32File.getRegisterByNumber(1).setValue(Memory32.DATA_SECTION_START);
        memory.setMultiple(Memory32.DATA_SECTION_START, 2, 1);
        handler.handle(lb);
        assertEquals(2, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void lbTruncateLittleEndian() throws MemoryAccessException, IllegalRegisterException {
        lb = new Lb(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 0));
        register32File.getRegisterByNumber(1).setValue(Memory32.DATA_SECTION_START);
        memory.setMultiple(Memory32.DATA_SECTION_START, 258 | (1 << 24), 4);
        handler.handle(lb);
        assertEquals(memory.isLittleEndian() ? 2 : 1, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void lbNegativeOffset() throws ImmediateTooLargeException, MemoryAccessException, IllegalRegisterException {
        lb = new Lb(new InstructionI.InstructionIParams(
            (byte) 0,
            (byte) 1,
            (short) RiscVInstruction.truncateNegative(-4, 12)
        ));
        register32File.getRegisterByNumber(1).setValue(Memory32.DATA_SECTION_START + 4);
        memory.setMultiple(Memory32.DATA_SECTION_START, 42, 1);
        handler.handle(lb);
        assertEquals(42, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void lbNegative() throws MemoryAccessException, IllegalRegisterException {
        lb = new Lb(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 0));
        register32File.getRegisterByNumber(1).setValue(Memory32.DATA_SECTION_START);
        memory.setMultiple(Memory32.DATA_SECTION_START, -1, 1);
        handler.handle(lb);
        assertEquals(-1, register32File.getRegisterByNumber(0).getValue());
    }
}
