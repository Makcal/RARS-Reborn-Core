package core.riscvprogram;

public interface IDataBlock {
    int size();

    byte alignment();

    byte[] value();
}
