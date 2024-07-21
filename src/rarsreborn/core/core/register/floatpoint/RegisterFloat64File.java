package rarsreborn.core.core.register.floatpoint;

import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import java.util.ArrayList;
import java.util.Collection;

public class RegisterFloat64File implements IRegisterFile<RegisterFloat64> {
    private final ArrayList<RegisterFloat64> registers = new ArrayList<>();

    public void addRegister(RegisterFloat64 register) {
        registers.add(register);
    }

    public void createRegistersFromNames(String ...names) {
        int offset = registers.size();
        for (int i = 0; i < names.length; i++) {
            registers.add(new RegisterFloat64(offset + i, names[i]));
        }
    }

    @Override
    public Collection<RegisterFloat64> getAllRegisters() {
        return registers;
    }

    @Override
    public RegisterFloat64 getRegisterByName(String name) throws IllegalRegisterException {
        for (RegisterFloat64 register : registers) {
            if (register.getName().equals(name)) {
                return register;
            }
        }
        throw new IllegalRegisterException(name);
    }

    @Override
    public RegisterFloat64 getRegisterByNumber(int number) throws IllegalRegisterException {
        try {
            return registers.get(number);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalRegisterException(number);
        }
    }
}
