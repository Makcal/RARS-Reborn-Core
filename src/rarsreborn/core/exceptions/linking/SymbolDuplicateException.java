package rarsreborn.core.exceptions.linking;

public class SymbolDuplicateException extends LinkingException {
    public SymbolDuplicateException(String symbol) {
        super("Symbol \"" + symbol + "\" is repeated across object files");
    }
}
