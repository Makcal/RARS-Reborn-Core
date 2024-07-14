package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Or;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrTest {
    private static final Register32File register32File = new Register32File();
    private Or or;
    private Or.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2", "r3");
    }

    @BeforeEach
    void init() {
        or = new Or(new InstructionR.InstructionRParams((byte) 0, (byte) 1, (byte) 2));
        handler = new Or.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void or() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(0b110);
        register32File.getRegisterByNumber(2).setValue(0b100);
        handler.handle(or);
        assertEquals(0b110, register32File.getRegisterByNumber(0).getValue());
    }
}
