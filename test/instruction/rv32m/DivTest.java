package instruction.rv32m;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32m.Div;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DivTest {
    private static final Register32File register32File = new Register32File();
    private Div div;
    private Div.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2", "r3");
    }

    @BeforeEach
    void init() {
        div = new Div(new InstructionR.InstructionRParams((byte) 0, (byte) 1, (byte) 2));
        handler = new Div.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void division() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(10);
        register32File.getRegisterByNumber(2).setValue(2);
        handler.handle(div);
        assertEquals(5, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void divisionNegative() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(-12);
        register32File.getRegisterByNumber(2).setValue(3);
        handler.handle(div);
        assertEquals(-4, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void divisionWithRemainder() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(14);
        register32File.getRegisterByNumber(2).setValue(5);
        handler.handle(div);
        assertEquals(2, register32File.getRegisterByNumber(0).getValue());
    }
}
