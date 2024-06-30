package rarsreborn.core.core.register;

public class ZeroRegister32 extends Register32 {
    public ZeroRegister32(int number, String name) {
        super(number, name);
    }

    public ZeroRegister32(int number, String name, int value) {
        super(number, name, value);
    }

    @Override
    public void setValue(int value) {
        // Do nothing. Unchangeable
    }

    @Override
    public int getValue() {
        return 0;
    }
}
