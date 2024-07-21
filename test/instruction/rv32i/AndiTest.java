package instruction.rv32i;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Andi;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static rarsreborn.core.core.instruction.riscv.RiscVInstruction.truncateNegative;

class AndiTest {
    private static final Register32File register32File = new Register32File();
    private Andi.Handler handler;

    @BeforeAll
    static void initAll() {
        register32File.createRegistersFromNames("r1", "r2");
    }

    @BeforeEach
    void init() {
        handler = new Andi.Handler();
        handler.attachRegisters(register32File);
    }

    @Test
    void and() throws IllegalRegisterException {
        Andi andi = new Andi(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 0b1010));
        register32File.getRegisterByNumber(1).setValue(0b0111);
        handler.handle(andi);
        assertEquals(0b0010, register32File.getRegisterByNumber(0).getValue());
    }

    @Test
    void andNegative() throws IllegalRegisterException, ImmediateTooLargeException {
        Andi andi = new Andi(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) truncateNegative(-2, 12)));
        register32File.getRegisterByNumber(1).setValue(0b0111);
        handler.handle(andi);
        assertEquals(0b0110, register32File.getRegisterByNumber(0).getValue());
    }
}
