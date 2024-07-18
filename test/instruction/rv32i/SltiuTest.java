package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.RiscVInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Sltiu;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SltiuTest {
    private static final Register32File register32File = new Register32File();
    private Sltiu.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2");
    }

    @BeforeEach
    void init() {
        handler = new Sltiu.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void sltiuLess() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(1);
        Sltiu sltiu = new Sltiu(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 2));
        handler.handle(sltiu);
        assertEquals(1, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void sltiuEqual() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(1);
        Sltiu sltiu = new Sltiu(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 1));
        handler.handle(sltiu);
        assertEquals(0, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void sltiuGreater() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(2);
        Sltiu sltiu = new Sltiu(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 1));
        handler.handle(sltiu);
        assertEquals(0, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void sltiuLessNegative() throws IllegalRegisterException, ImmediateTooLargeException {
        register32File.getRegisterByNumber(1).setValue(1);
        Sltiu sltiu = new Sltiu(new InstructionI.InstructionIParams(
            (byte) 0,
            (byte) 1,
            (short) RiscVInstruction.truncateNegative(-1, 12))
        );
        handler.handle(sltiu);
        assertEquals(1, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void sltiuGreaterNegative() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(-1);
        Sltiu sltiu = new Sltiu(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 1));
        handler.handle(sltiu);
        assertEquals(0, register32File.getRegisterByNumber(0).getValue());
    }
}
