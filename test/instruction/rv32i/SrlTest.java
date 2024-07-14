package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Srl;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SrlTest {
    private static final Register32File register32File = new Register32File();
    private Srl srl;
    private Srl.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2", "r3");
    }

    @BeforeEach
    void init() {
        srl = new Srl(new InstructionR.InstructionRParams((byte) 0, (byte) 1, (byte) 2));
        handler = new Srl.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void srl() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(0b110);
        register32File.getRegisterByNumber(2).setValue(2);
        handler.handle(srl);
        assertEquals(0b1, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void srlNegative() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(-2);
        register32File.getRegisterByNumber(2).setValue(1);
        handler.handle(srl);
        assertEquals(Integer.MAX_VALUE, register32File.getRegisterByNumber(0).getValue());
    }
}
