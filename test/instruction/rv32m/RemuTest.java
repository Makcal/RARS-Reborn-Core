package instruction.rv32m;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32m.Remu;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RemuTest {
    private static final Register32File register32File = new Register32File();
    private Remu remu;
    private Remu.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2", "r3");
    }

    @BeforeEach
    void init() {
        remu = new Remu(new InstructionR.InstructionRParams((byte) 0, (byte) 1, (byte) 2));
        handler = new Remu.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void remu() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(10);
        register32File.getRegisterByNumber(2).setValue(3);
        handler.handle(remu);
        assertEquals(1, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void remuNegative() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(-1);
        register32File.getRegisterByNumber(2).setValue(4);
        handler.handle(remu);
        assertEquals(3, register32File.getRegisterByNumber(0).getValue());
    }
}
