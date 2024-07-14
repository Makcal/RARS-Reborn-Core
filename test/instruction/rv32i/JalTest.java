package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionJ;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Jal;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static rarsreborn.core.core.instruction.riscv.RiscVInstruction.truncateNegative;

class JalTest {
    public static final Register32 programCounter = new Register32(2, "pc");
    private static final Register32File register32File = new Register32File();
    private static final Jal.Handler handler = new Jal.Handler();
    private Jal jal;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("rd");
        handler.attachRegisters(register32File);
        handler.attachProgramCounter(programCounter);
    }

    @BeforeEach
    void init() {
        programCounter.setValue(0x400_000);
    }

    @Test
    void jal() throws IllegalRegisterException {
        jal = new Jal(new InstructionJ.InstructionJParams((byte) 0, 12));
        handler.handle(jal);
        assertEquals(0x400_004, register32File.getRegisterByNumber(0).getValue());
        assertEquals(0x400_000 + 12, programCounter.getValue());
    }

    @Test
    void jalNegative() throws ImmediateTooLargeException, IllegalRegisterException {
        jal = new Jal(new InstructionJ.InstructionJParams((byte) 0, (int) truncateNegative(-2, 21)));
        handler.handle(jal);
        assertEquals(0x400_004, register32File.getRegisterByNumber(0).getValue());
        assertEquals(0x400_000 - 2, programCounter.getValue());
    }
}
