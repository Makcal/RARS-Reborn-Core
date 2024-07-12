package rarsreborn.core.core.memory;

public record Memory32ChangeEvent(long address, byte[] oldSpan, byte[] newSpan) {}
