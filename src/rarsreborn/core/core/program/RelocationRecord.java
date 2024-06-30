package rarsreborn.core.core.program;

public record RelocationRecord(long offset, String symbol, byte extraCompensation) {
    public RelocationRecord(long offset, String symbol) {
        this(offset, symbol, (byte) 0);
    }
}
