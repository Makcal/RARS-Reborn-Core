package rarsreborn.core.core.instruction;

public interface IInstruction {
    String getName();

    byte[] serialize();
}
