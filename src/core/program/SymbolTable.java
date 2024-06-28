package core.program;

import exceptions.compilation.LabelDuplicateException;
import exceptions.compilation.LabelNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable implements ISymbolTable {
    private final Map<String, Long> symbolTable = new HashMap<>();

    @Override
    public void addSymbol(String symbol, long address) throws LabelDuplicateException {
        if (symbolTable.containsKey(symbol)) {
            throw new LabelDuplicateException(symbol);
        }
        symbolTable.put(symbol, address);
    }

    @Override
    public long getSymbol(String symbol) throws LabelNotFoundException {
        if (!symbolTable.containsKey(symbol)) {
            throw new LabelNotFoundException(symbol);
        }
        return symbolTable.get(symbol);
    }
}
