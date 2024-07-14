package rarsreborn.core.core.register;

import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import java.util.Collection;

public interface IRegisterFile<TRegister extends IRegister> {
    Collection<TRegister> getAllRegisters();

    TRegister getRegisterByName(String name) throws IllegalRegisterException;

    TRegister getRegisterByNumber(int number) throws IllegalRegisterException;

    default void reset() {
        for (TRegister register : getAllRegisters()) {
            register.reset();
        }
    }
}
