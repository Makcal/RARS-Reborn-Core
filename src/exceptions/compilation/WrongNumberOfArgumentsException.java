package exceptions.compilation;

public class WrongNumberOfArgumentsException extends CompilationException {
    public WrongNumberOfArgumentsException(String instructionName, int given, int expected) {
        super(instructionName + " expects " + expected + " arguments. Given: " + given);
    }
}
