package rarsreborn.core.core.memory;

public record MemoryChangeEvent(long address, byte[] oldSpan, byte[] newSpan) {}
