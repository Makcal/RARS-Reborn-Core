package rarsreborn.core.exceptions.compilation;

public class WrongNumberOfArgumentsException extends CompilationException {
    public WrongNumberOfArgumentsException(String instructionName, int given, int expected) {
        super(instructionName + " expects " + expected + " arguments. Given: " + given);
    }

    public WrongNumberOfArgumentsException(String instructionName, String line, String expected) {
        super(instructionName + " expects " + expected + " arguments. Arguments: " + line);
    }
}
