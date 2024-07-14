package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionB;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Blt;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.compilation.UnknownRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BltTest {
    public static final Register32 programCounter = new Register32(2, "pc");
    private static final Register32File register32File = new Register32File();
    private static final Blt.Handler handler = new Blt.Handler();
    private static final int PC_INITIAL = 0x400_000; 
    private static final int PC_CHANGED = 0x400_100; 
    private static final Blt blt = new Blt(new InstructionB.InstructionBParams(
        (byte) 0,
        (byte) 1,
        (short) ((PC_CHANGED - PC_INITIAL) >> 1)
    ));

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("rs1", "rs2");
        handler.attachRegisters(register32File);
        handler.attachProgramCounter(programCounter);
    }

    @BeforeEach
    void init() {
        programCounter.setValue(PC_INITIAL);
    }

    @Test
    void bltSuccessful() throws UnknownRegisterException {
        register32File.getRegisterByNumber(0).setValue(1);
        register32File.getRegisterByNumber(1).setValue(2);
        handler.handle(blt);
        assertEquals(PC_CHANGED, programCounter.getValue());
    }

    @Test
    void bltUnsuccessfulEqual() throws UnknownRegisterException {
        register32File.getRegisterByNumber(0).setValue(1);
        register32File.getRegisterByNumber(1).setValue(1);
        handler.handle(blt);
        assertEquals(PC_INITIAL, programCounter.getValue());
    }

    @Test
    void bltUnsuccessful() throws UnknownRegisterException {
        register32File.getRegisterByNumber(0).setValue(2);
        register32File.getRegisterByNumber(1).setValue(1);
        handler.handle(blt);
        assertEquals(PC_INITIAL, programCounter.getValue());
    }

    @Test
    void bltSuccessfulNegative() throws UnknownRegisterException {
        register32File.getRegisterByNumber(0).setValue(-1);
        register32File.getRegisterByNumber(1).setValue(2);
        handler.handle(blt);
        assertEquals(PC_CHANGED, programCounter.getValue());
    }

    @Test
    void bltUnsuccessfulNegative() throws UnknownRegisterException {
        register32File.getRegisterByNumber(0).setValue(2);
        register32File.getRegisterByNumber(1).setValue(-1);
        handler.handle(blt);
        assertEquals(PC_INITIAL, programCounter.getValue());
    }
}
