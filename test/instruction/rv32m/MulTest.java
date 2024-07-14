package instruction.rv32m;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32m.Mul;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MulTest {
    private static final Register32File register32File = new Register32File();
    private Mul mul;
    private Mul.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2", "r3");
    }

    @BeforeEach
    void init() {
        mul = new Mul(new InstructionR.InstructionRParams((byte) 0, (byte) 1, (byte) 2));
        handler = new Mul.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void multiply() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(3);
        register32File.getRegisterByNumber(2).setValue(2);
        handler.handle(mul);
        assertEquals(6, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void multiplyNegative() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(-3);
        register32File.getRegisterByNumber(2).setValue(-2);
        handler.handle(mul);
        assertEquals(6, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void multiplyOverflow() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(Integer.MAX_VALUE);
        register32File.getRegisterByNumber(2).setValue(4);
        handler.handle(mul);
        //noinspection NumericOverflow
        assertEquals(Integer.MAX_VALUE << 2, register32File.getRegisterByNumber(0).getValue());
    }
}
