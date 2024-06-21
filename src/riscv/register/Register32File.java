package riscv.register;

import exceptions.compilation.UnknownRegisterException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class Register32File implements IRegisterFile<Register32> {
    private final Register32[] registers;

    public Register32File(Collection<String> names) {
        this.registers = new Register32[names.size()];
        Iterator<String> iterator = names.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            this.registers[i] = new Register32(i, iterator.next());
        }
    }

    @Override
    public Collection<Register32> getAllRegisters() {
        return Arrays.asList(registers);
    }

    @Override
    public Register32 getRegisterByName(String name) throws UnknownRegisterException {
        for (Register32 register : registers) {
            if (register.getName().equals(name)) {
                return register;
            }
        }
        throw new UnknownRegisterException("Register not found: " + name);
    }

    /*
     * Something strange with unreachable code warning.
     */
    @SuppressWarnings("UnreachableCode")
    @Override
    public Register32 getRegisterByNumber(int number) throws UnknownRegisterException {
        try {
            return registers[number];
        } catch (IndexOutOfBoundsException e) {
            throw new UnknownRegisterException(String.valueOf(number));
        }
    }
}
