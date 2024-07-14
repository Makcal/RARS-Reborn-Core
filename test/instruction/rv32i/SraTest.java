package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Sra;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SraTest {
    private static final Register32File register32File = new Register32File();
    private Sra sra;
    private Sra.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2", "r3");
    }

    @BeforeEach
    void init() {
        sra = new Sra(new InstructionR.InstructionRParams((byte) 0, (byte) 1, (byte) 2));
        handler = new Sra.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void sra() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(0b110);
        register32File.getRegisterByNumber(2).setValue(2);
        handler.handle(sra);
        assertEquals(0b1, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void sraNegative() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(-2);
        register32File.getRegisterByNumber(2).setValue(1);
        handler.handle(sra);
        assertEquals(-1, register32File.getRegisterByNumber(0).getValue());
    }
}
