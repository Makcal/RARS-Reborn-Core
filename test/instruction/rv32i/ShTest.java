package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.RiscVInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionS;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Sh;
import rarsreborn.core.core.memory.Memory32;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;
import rarsreborn.core.exceptions.memory.MemoryAccessException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShTest {
    private static final Register32File register32File = new Register32File();
    private static final Memory32 memory = new Memory32();
    private Sh sh;
    private Sh.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2");
    }

    @BeforeEach
    void init() {
        handler = new Sh.Handler();
        handler.attachRegisters(register32File);
        handler.attachMemory(memory);
    }

    @Test
    void sh() throws MemoryAccessException, IllegalRegisterException {
        sh = new Sh(new InstructionS.InstructionSParams((byte) 0, (byte) 1, (short) 0));
        register32File.getRegisterByNumber(0).setValue(Memory32.DATA_SECTION_START);
        register32File.getRegisterByNumber(1).setValue(257);
        handler.handle(sh);
        assertEquals(257, memory.getMultiple(Memory32.DATA_SECTION_START, 2));
    }

    @Test
    void shNegative() throws ImmediateTooLargeException, MemoryAccessException, IllegalRegisterException {
        sh = new Sh(new InstructionS.InstructionSParams(
            (byte) 0,
            (byte) 1,
            (short) RiscVInstruction.truncateNegative(-4, 12)
        ));
        register32File.getRegisterByNumber(0).setValue(Memory32.DATA_SECTION_START + 4);
        register32File.getRegisterByNumber(1).setValue(-1);
        handler.handle(sh);
        assertEquals(-1, (short) memory.getMultiple(Memory32.DATA_SECTION_START, 2));
    }
}
