package rarsreborn.core.core.register;

public record Register32ChangeEvent(Register32 register, int oldValue, int newValue) {}
