package core.program;

import exceptions.compilation.LabelDuplicateException;
import exceptions.linking.LabelNotFoundException;

import java.util.List;

public interface ISymbolTable {
    void addSymbol(Symbol symbol) throws LabelDuplicateException;

    Symbol getSymbol(String name) throws LabelNotFoundException;

    List<Symbol> getAllSymbols();
}
