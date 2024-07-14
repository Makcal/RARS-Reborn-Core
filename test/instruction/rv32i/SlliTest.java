package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Slli;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlliTest {
    private static final Register32File register32File = new Register32File();
    private Slli.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2");
    }

    @BeforeEach
    void init() {
        handler = new Slli.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void sll() throws IllegalRegisterException {
        Slli slli = new Slli(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 2));
        register32File.getRegisterByNumber(1).setValue(0b1010);
        handler.handle(slli);
        assertEquals(0b101000, register32File.getRegisterByNumber(0).getValue());
    }
}
