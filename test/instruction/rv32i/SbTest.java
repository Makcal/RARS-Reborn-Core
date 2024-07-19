package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.RiscVInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionS;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Sb;
import rarsreborn.core.core.memory.Memory32;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;
import rarsreborn.core.exceptions.memory.MemoryAccessException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SbTest {
    private static final Register32File register32File = new Register32File();
    private static final Memory32 memory = new Memory32();
    private Sb sb;
    private Sb.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2");
    }

    @BeforeEach
    void init() {
        handler = new Sb.Handler();
        handler.attachRegisters(register32File);
        handler.attachMemory(memory);
    }

    @Test
    void sb() throws MemoryAccessException, IllegalRegisterException {
        sb = new Sb(new InstructionS.InstructionSParams((byte) 0, (byte) 1, (short) 0));
        register32File.getRegisterByNumber(0).setValue(Memory32.DATA_SECTION_START);
        register32File.getRegisterByNumber(1).setValue(257);
        handler.handle(sb);
        assertEquals(1, memory.getMultiple(Memory32.DATA_SECTION_START, 1));
    }

    @Test
    void sbNegative() throws ImmediateTooLargeException, MemoryAccessException, IllegalRegisterException {
        sb = new Sb(new InstructionS.InstructionSParams(
            (byte) 0,
            (byte) 1,
            (short) RiscVInstruction.truncateNegative(-4, 12)
        ));
        register32File.getRegisterByNumber(0).setValue(Memory32.DATA_SECTION_START + 4);
        register32File.getRegisterByNumber(1).setValue(-1);
        handler.handle(sb);
        assertEquals(-1, (byte) memory.getMultiple(Memory32.DATA_SECTION_START, 1));
    }
}
