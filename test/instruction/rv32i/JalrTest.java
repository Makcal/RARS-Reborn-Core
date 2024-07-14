package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Jalr;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.compilation.UnknownRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static rarsreborn.core.core.instruction.riscv.RiscVInstruction.truncateNegative;

class JalrTest {
    public static final Register32 programCounter = new Register32(2, "pc");
    private static final Register32File register32File = new Register32File();
    private static final Jalr.Handler handler = new Jalr.Handler();
    private static final int RS1_VALUE = 0x400_008;
    private Jalr jalr;

    @BeforeAll
    static void initAll() throws UnknownRegisterException {
        register32File.createRegistersFromNames("rd", "rs1");
        register32File.getRegisterByNumber(1).setValue(RS1_VALUE);
        handler.attachRegisters(register32File);
        handler.attachProgramCounter(programCounter);
    }

    @BeforeEach
    void init() {
        programCounter.setValue(0x400_000);
    }

    @Test
    void jalr() throws UnknownRegisterException {
        jalr = new Jalr(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 8));
        handler.handle(jalr);
        assertEquals(0x400_004, register32File.getRegisterByNumber(0).getValue());
        assertEquals(RS1_VALUE + 8, programCounter.getValue());
    }

    @Test
    void jalrNegative() throws UnknownRegisterException, ImmediateTooLargeException {
        jalr = new Jalr(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) truncateNegative(-2, 12)));
        handler.handle(jalr);
        assertEquals(0x400_004, register32File.getRegisterByNumber(0).getValue());
        assertEquals(RS1_VALUE - 2, programCounter.getValue());
    }
}
