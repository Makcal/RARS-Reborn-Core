package riscv.register;

public class Register32 implements IIntegerRegister {
    private final int number;
    private final String name;
    private int value;

    public Register32(int number, String name) {
        this(number, name, 0);
    }

    public Register32(int number, String name, int value) {
        this.number = number;
        this.name = name;
        this.value = value;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
