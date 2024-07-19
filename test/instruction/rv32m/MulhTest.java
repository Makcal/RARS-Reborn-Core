package instruction.rv32m;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32m.Mulh;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MulhTest {
    private static final Register32File register32File = new Register32File();
    private Mulh mulh;
    private Mulh.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2", "r3");
    }

    @BeforeEach
    void init() {
        mulh = new Mulh(new InstructionR.InstructionRParams((byte) 0, (byte) 1, (byte) 2));
        handler = new Mulh.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void multiplyHigh() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(3);
        register32File.getRegisterByNumber(2).setValue(2);
        handler.handle(mulh);
        assertEquals(0, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void multiplyHighNegative() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(-3);
        register32File.getRegisterByNumber(2).setValue(-2);
        handler.handle(mulh);
        assertEquals(0, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void multiplyHighOverflow() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(Integer.MAX_VALUE);
        register32File.getRegisterByNumber(2).setValue(4);
        handler.handle(mulh);
        assertEquals(1, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void multiplyHighOverflowNegative() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(-1);
        register32File.getRegisterByNumber(2).setValue(4);
        handler.handle(mulh);
        assertEquals(-1, register32File.getRegisterByNumber(0).getValue());
    }
}
