package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Add;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.compilation.UnknownRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddTest {
    private static final Register32File register32File = new Register32File();
    private Add add;
    private Add.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2", "r3");
    }

    @BeforeEach
    void init() {
        add = new Add(new InstructionR.InstructionRParams((byte) 0, (byte) 1, (byte) 2));
        handler = new Add.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void addition() throws UnknownRegisterException {
        register32File.getRegisterByNumber(1).setValue(1);
        register32File.getRegisterByNumber(2).setValue(2);
        handler.handle(add);
        assertEquals(3, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void additionNegative() throws UnknownRegisterException {
        register32File.getRegisterByNumber(1).setValue(-1);
        register32File.getRegisterByNumber(2).setValue(-2);
        handler.handle(add);
        assertEquals(-3, register32File.getRegisterByNumber(0).getValue());
    }
}
