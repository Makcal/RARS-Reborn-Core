package exceptions.compilation;

import riscv.register.IRegister;

public class WrongRegisterTypeException extends CompilationException {
    public WrongRegisterTypeException(Class<? extends IRegister> expected, Class<? extends IRegister> given) {
        super("Expected register of type " + expected.getSimpleName() + ", but got " + given.getSimpleName());
    }
}
