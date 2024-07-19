package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.RiscVInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Slti;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SltiTest {
    private static final Register32File register32File = new Register32File();
    private Slti.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2");
    }

    @BeforeEach
    void init() {
        handler = new Slti.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void sltiLess() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(1);
        Slti slti = new Slti(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 2));
        handler.handle(slti);
        assertEquals(1, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void sltiEqual() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(1);
        Slti slti = new Slti(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 1));
        handler.handle(slti);
        assertEquals(0, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void sltiGreater() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(2);
        Slti slti = new Slti(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 1));
        handler.handle(slti);
        assertEquals(0, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void sltiLessNegative() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(-1);
        Slti slti = new Slti(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 1));
        handler.handle(slti);
        assertEquals(1, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void sltiGreaterNegative() throws IllegalRegisterException, ImmediateTooLargeException {
        register32File.getRegisterByNumber(1).setValue(1);
        Slti slti = new Slti(new InstructionI.InstructionIParams(
            (byte) 0,
            (byte) 1,
            (short) RiscVInstruction.truncateNegative(-1, 12))
        );
        handler.handle(slti);
        assertEquals(0, register32File.getRegisterByNumber(0).getValue());
    }
}
