package instruction;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Sub;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.compilation.UnknownRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubTest {
    private static final Register32File register32File = new Register32File();
    private Sub Sub;
    private Sub.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2", "r3");
    }

    @BeforeEach
    void init() {
        Sub = new Sub(new InstructionR.InstructionRParams((byte) 0, (byte) 1, (byte) 2));
        handler = new Sub.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void subtraction() throws UnknownRegisterException {
        register32File.getRegisterByNumber(1).setValue(4);
        register32File.getRegisterByNumber(2).setValue(2);
        handler.handle(Sub);
        assertEquals(2, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void subtractionNegative() throws UnknownRegisterException {
        register32File.getRegisterByNumber(1).setValue(-1);
        register32File.getRegisterByNumber(2).setValue(2);
        handler.handle(Sub);
        assertEquals(-3, register32File.getRegisterByNumber(0).getValue());
    }
}
