package rarsreborn.core.core.register;

import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public interface IRegisterCollection {
    IRegister findRegister(String name) throws IllegalRegisterException;
}
