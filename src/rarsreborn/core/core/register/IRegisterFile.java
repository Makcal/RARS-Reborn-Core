package rarsreborn.core.core.register;

import rarsreborn.core.exceptions.compilation.UnknownRegisterException;

import java.util.Collection;

public interface IRegisterFile<TRegister extends IRegister> {
    Collection<TRegister> getAllRegisters();

    TRegister getRegisterByName(String name) throws UnknownRegisterException;

    TRegister getRegisterByNumber(int number) throws UnknownRegisterException;

    default void reset() {
        for (TRegister register : getAllRegisters()) {
            register.reset();
        }
    }
}
