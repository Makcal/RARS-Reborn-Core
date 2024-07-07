package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Sll;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.compilation.UnknownRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SllTest {
    private static final Register32File register32File = new Register32File();
    private Sll sll;
    private Sll.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2", "r3");
    }

    @BeforeEach
    void init() {
        sll = new Sll(new InstructionR.InstructionRParams((byte) 0, (byte) 1, (byte) 2));
        handler = new Sll.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void sll() throws UnknownRegisterException {
        register32File.getRegisterByNumber(1).setValue(0b110);
        register32File.getRegisterByNumber(2).setValue(2);
        handler.handle(sll);
        assertEquals(0b11000, register32File.getRegisterByNumber(0).getValue());
    }
}
