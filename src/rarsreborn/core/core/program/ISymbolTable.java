package rarsreborn.core.core.program;

import rarsreborn.core.exceptions.compilation.LabelDuplicateException;
import rarsreborn.core.exceptions.linking.LabelNotFoundException;

import java.util.List;

public interface ISymbolTable {
    void addSymbol(Symbol symbol) throws LabelDuplicateException;

    Symbol getSymbol(String name) throws LabelNotFoundException;

    List<Symbol> getAllSymbols();
}
