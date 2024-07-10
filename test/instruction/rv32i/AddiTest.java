package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.RiscVInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Addi;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.compilation.UnknownRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddiTest {
    private static final Register32File register32File = new Register32File();
    private Addi addi;
    private Addi.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2");
    }

    @BeforeEach
    void init() {
        handler = new Addi.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void addition() throws UnknownRegisterException {
        addi = new Addi(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 10));
        register32File.getRegisterByNumber(1).setValue(1);
        handler.handle(addi);
        assertEquals(11, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void additionNegative() throws UnknownRegisterException, ImmediateTooLargeException {
        addi = new Addi(new InstructionI.InstructionIParams(
            (byte) 0,
            (byte) 1,
            (short) RiscVInstruction.truncateNegative(-10, 12)
        ));
        register32File.getRegisterByNumber(1).setValue(3);
        handler.handle(addi);
        assertEquals(-7, register32File.getRegisterByNumber(0).getValue());
    }
}
