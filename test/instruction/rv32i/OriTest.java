package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Ori;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OriTest {
    private static final Register32File register32File = new Register32File();
    private Ori.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2");
    }

    @BeforeEach
    void init() {
        handler = new Ori.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void or() throws IllegalRegisterException {
        Ori ori = new Ori(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 0b1010));
        register32File.getRegisterByNumber(1).setValue(0b0110);
        handler.handle(ori);
        assertEquals(0b1110, register32File.getRegisterByNumber(0).getValue());
    }
}
