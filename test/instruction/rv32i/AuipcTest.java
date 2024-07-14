package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionU;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Auipc;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuipcTest {
    private static final Register32File register32File = new Register32File();
    private static Auipc.Handler handler;
    private Auipc auipc;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("rd");
        Register32 programCounter = new Register32(2, "pc", 0x400_000);
        handler = new Auipc.Handler();
        handler.attachRegisters(register32File);
        handler.attachProgramCounter(programCounter);
    }

    @Test
    void auipc() throws IllegalRegisterException {
        auipc = new Auipc(new InstructionU.InstructionUParams((byte) 0, 8 << 12));
        handler.handle(auipc);
        assertEquals(0x400_000 + (8 << 12), register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void auipcNegative() throws IllegalRegisterException {
        auipc = new Auipc(new InstructionU.InstructionUParams((byte) 0, -2 << 12));
        handler.handle(auipc);
        assertEquals(0x400_000 - (2 << 12), register32File.getRegisterByNumber(0).getValue());
    }
}
