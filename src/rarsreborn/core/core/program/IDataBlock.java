package rarsreborn.core.core.program;

public interface IDataBlock {
    int getSize();

    byte getAlignment();

    byte[] getValue();
}
