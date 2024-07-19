package instruction.rv32m;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32m.Rem;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RemTest {
    private static final Register32File register32File = new Register32File();
    private Rem rem;
    private Rem.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2", "r3");
    }

    @BeforeEach
    void init() {
        rem = new Rem(new InstructionR.InstructionRParams((byte) 0, (byte) 1, (byte) 2));
        handler = new Rem.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void remainder() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(10);
        register32File.getRegisterByNumber(2).setValue(3);
        handler.handle(rem);
        assertEquals(1, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void remainderNegative() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(-12);
        register32File.getRegisterByNumber(2).setValue(5);
        handler.handle(rem);
        assertEquals(-2, register32File.getRegisterByNumber(0).getValue());
    }
}
