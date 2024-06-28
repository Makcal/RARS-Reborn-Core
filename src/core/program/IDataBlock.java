package core.program;

public interface IDataBlock {
    int getSize();

    byte getAlignment();

    byte[] getValue();
}
