package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.ShiftRightImm;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SraiTest {
    private static final Register32File register32File = new Register32File();
    private ShiftRightImm.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2");
    }

    @BeforeEach
    void init() {
        handler = new ShiftRightImm.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void sra() throws IllegalRegisterException {
        ShiftRightImm srai = new ShiftRightImm(new InstructionI.InstructionIParams(
            (byte) 0,
            (byte) 1,
            (short) (2 | 1 << 10)
        ));
        register32File.getRegisterByNumber(1).setValue(0b1010);
        handler.handle(srai);
        assertEquals(0b10, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void sraNegative() throws IllegalRegisterException {
        ShiftRightImm srai = new ShiftRightImm(new InstructionI.InstructionIParams(
            (byte) 0,
            (byte) 1,
            (short) (2 | 1 << 10)
        ));
        register32File.getRegisterByNumber(1).setValue(-2);
        handler.handle(srai);
        assertEquals(-1, register32File.getRegisterByNumber(0).getValue());
    }
}
