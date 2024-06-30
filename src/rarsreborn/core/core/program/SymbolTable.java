package rarsreborn.core.core.program;

import rarsreborn.core.exceptions.compilation.LabelDuplicateException;
import rarsreborn.core.exceptions.linking.LabelNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable implements ISymbolTable {
    private final Map<String, Symbol> symbolTable = new HashMap<>();

    @Override
    public void addSymbol(Symbol symbol) throws LabelDuplicateException {
        if (symbolTable.containsKey(symbol.name())) {
            throw new LabelDuplicateException(symbol.name());
        }
        symbolTable.put(symbol.name(), symbol);
    }

    @Override
    public Symbol getSymbol(String name) throws LabelNotFoundException {
        if (!symbolTable.containsKey(name)) {
            throw new LabelNotFoundException(name);
        }
        return symbolTable.get(name);
    }

    @Override
    public List<Symbol> getAllSymbols() {
        return new ArrayList<>(symbolTable.values());
    }
}
