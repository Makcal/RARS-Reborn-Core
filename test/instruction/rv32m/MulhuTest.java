package instruction.rv32m;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32m.Mulhu;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MulhuTest {
    private static final Register32File register32File = new Register32File();
    private Mulhu mulhu;
    private Mulhu.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2", "r3");
    }

    @BeforeEach
    void init() {
        mulhu = new Mulhu(new InstructionR.InstructionRParams((byte) 0, (byte) 1, (byte) 2));
        handler = new Mulhu.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void mulu() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(3);
        register32File.getRegisterByNumber(2).setValue(2);
        handler.handle(mulhu);
        assertEquals(0, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void muluNegative() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(-3);
        register32File.getRegisterByNumber(2).setValue(-2);
        handler.handle(mulhu);
        assertEquals(-5, register32File.getRegisterByNumber(0).getValue());
    }
}
