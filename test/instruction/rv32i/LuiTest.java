package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionU;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Lui;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LuiTest {
    private static final Register32File register32File = new Register32File();
    private static Lui.Handler handler;
    private Lui lui;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("rd");
        handler = new Lui.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void lui() throws IllegalRegisterException {
        lui = new Lui(new InstructionU.InstructionUParams((byte) 0, 8 << 12));
        handler.handle(lui);
        assertEquals(8 << 12, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void luiNegative() throws IllegalRegisterException {
        lui = new Lui(new InstructionU.InstructionUParams((byte) 0, -2 << 12));
        handler.handle(lui);
        assertEquals(-2 << 12, register32File.getRegisterByNumber(0).getValue());
    }
}
