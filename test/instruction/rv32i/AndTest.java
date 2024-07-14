package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.And;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AndTest {
    private static final Register32File register32File = new Register32File();
    private And and;
    private And.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2", "r3");
    }

    @BeforeEach
    void init() {
        and = new And(new InstructionR.InstructionRParams((byte) 0, (byte) 1, (byte) 2));
        handler = new And.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void and() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(0b110);
        register32File.getRegisterByNumber(2).setValue(0b100);
        handler.handle(and);
        assertEquals(0b100, register32File.getRegisterByNumber(0).getValue());
    }
}
