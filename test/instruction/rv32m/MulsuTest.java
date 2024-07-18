package instruction.rv32m;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32m.Mulsu;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MulsuTest {
    private static final Register32File register32File = new Register32File();
    private Mulsu mulsu;
    private Mulsu.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2", "r3");
    }

    @BeforeEach
    void init() {
        mulsu = new Mulsu(new InstructionR.InstructionRParams((byte) 0, (byte) 1, (byte) 2));
        handler = new Mulsu.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void mulsu() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(3);
        register32File.getRegisterByNumber(2).setValue(2);
        handler.handle(mulsu);
        assertEquals(0, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void mulsuNegativeFirst() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(-1);
        register32File.getRegisterByNumber(2).setValue(5);
        handler.handle(mulsu);
        assertEquals(-1, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void mulsuLargeSecond() throws IllegalRegisterException {
        register32File.getRegisterByNumber(1).setValue(4);
        register32File.getRegisterByNumber(2).setValue(-1);
        handler.handle(mulsu);
        assertEquals(3, register32File.getRegisterByNumber(0).getValue());
    }
}
