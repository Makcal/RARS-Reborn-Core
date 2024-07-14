package rarsreborn.core.core.register;

import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import java.util.ArrayList;
import java.util.Collection;

public class Register32File implements IRegisterFile<Register32> {
    private final ArrayList<Register32> registers = new ArrayList<>();

    public void addRegister(Register32 register) {
        registers.add(register);
    }

    public void createRegistersFromNames(String ...names) {
        int offset = registers.size();
        for (int i = 0; i < names.length; i++) {
            registers.add(new Register32(offset + i, names[i]));
        }
    }

    @Override
    public Collection<Register32> getAllRegisters() {
        return registers;
    }

    @Override
    public Register32 getRegisterByName(String name) throws IllegalRegisterException {
        for (Register32 register : registers) {
            if (register.getName().equals(name)) {
                return register;
            }
        }
        throw new IllegalRegisterException(name);
    }

    @Override
    public Register32 getRegisterByNumber(int number) throws IllegalRegisterException {
        try {
            return registers.get(number);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalRegisterException(number);
        }
    }
}
