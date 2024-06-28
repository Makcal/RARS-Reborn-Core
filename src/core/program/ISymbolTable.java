package core.program;

import exceptions.compilation.LabelDuplicateException;
import exceptions.compilation.LabelNotFoundException;

public interface ISymbolTable {
    void addSymbol(String symbol, long address) throws LabelDuplicateException;

    long getSymbol(String symbol) throws LabelNotFoundException;
}
