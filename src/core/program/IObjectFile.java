package core.program;

public interface IObjectFile {
    ISymbolTable getSymbolTable();

    IRelocationTable getRelocationTable();

    byte[] getData();

    byte[] getText();
}
