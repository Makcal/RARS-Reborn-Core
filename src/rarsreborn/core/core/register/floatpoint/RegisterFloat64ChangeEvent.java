package rarsreborn.core.core.register.floatpoint;

public record RegisterFloat64ChangeEvent(RegisterFloat64 register, float oldFloatValue, double oldDoubleValue,
                                         float newFloatValue, double newDoubleValue) {}
