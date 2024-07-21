package rarsreborn.core.core.register;

public interface IIntegerRegister extends IRegister {
    @Override
    default String getNumericName() {
        return "x" + getNumber();
    }
}
