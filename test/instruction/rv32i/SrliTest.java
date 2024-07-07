package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Srli;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.compilation.UnknownRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SrliTest {
    private static final Register32File register32File = new Register32File();
    private Srli.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2");
    }

    @BeforeEach
    void init() {
        handler = new Srli.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void srl() throws UnknownRegisterException {
        Srli srli = new Srli(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 2));
        register32File.getRegisterByNumber(1).setValue(0b1010);
        handler.handle(srli);
        assertEquals(0b10, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void srlNegative() throws UnknownRegisterException {
        Srli srli = new Srli(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 2));
        register32File.getRegisterByNumber(1).setValue(-2);
        handler.handle(srli);
        assertEquals(Integer.MAX_VALUE >> 1, register32File.getRegisterByNumber(0).getValue());
    }
}
