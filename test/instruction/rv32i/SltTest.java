package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Slt;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SltTest {
    private static final Register32File register32File = new Register32File();
    private Slt slt;
    private Slt.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2", "r3");
    }

    @BeforeEach
    void init() {
        slt = new Slt(new InstructionR.InstructionRParams((byte) 0, (byte) 1, (byte) 2));
        handler = new Slt.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void sltLess() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(1);
        register32File.getRegisterByNumber(2).setValue(2);
        handler.handle(slt);
        assertEquals(1, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void sltEqual() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(1);
        register32File.getRegisterByNumber(2).setValue(1);
        handler.handle(slt);
        assertEquals(0, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void sltGreater() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(2);
        register32File.getRegisterByNumber(2).setValue(1);
        handler.handle(slt);
        assertEquals(0, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void sltLessNegative() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(-1);
        register32File.getRegisterByNumber(2).setValue(1);
        handler.handle(slt);
        assertEquals(1, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void sltGreaterNegative() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(1);
        register32File.getRegisterByNumber(2).setValue(-1);
        handler.handle(slt);
        assertEquals(0, register32File.getRegisterByNumber(0).getValue());
    }
}
