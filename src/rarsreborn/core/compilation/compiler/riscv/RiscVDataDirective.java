package rarsreborn.core.compilation.compiler.riscv;

import rarsreborn.core.exceptions.compilation.SyntaxErrorException;
import rarsreborn.core.exceptions.compilation.UnknownDirectiveException;

public enum RiscVDataDirective {
    BYTE(".byte", (byte) 1),
    HALF(".half", (byte) 2),
    WORD(".word", (byte) 4),
    DWORD(".dword", (byte) 4),
    FLOAT(".float", (byte) 4),
    DOUBLE(".double", (byte) 4),
    SPACE(".space", (byte) 1),
    ASCII(".ascii", (byte) 1),
    ASCIZ(".asciz", (byte) 1),
    STRING(".string", (byte) 1);

    public final String text;
    public final byte alignment;

    RiscVDataDirective(String text, byte alignment) {
        this.text = text;
        this.alignment = alignment;
    }

    public static RiscVDataDirective parseName(String s) throws UnknownDirectiveException {
        for (RiscVDataDirective directive : values()) if (directive.text.equals(s)) return directive;
        throw new UnknownDirectiveException(s);
    }

    public byte[] parseValue(String s) throws SyntaxErrorException {
        long n;
        String parsedString;
        switch (this) {
            case BYTE:
                n = RegexCompiler.parseLongInteger(s);
                if ((n ^ (n & 0xFF)) == 0) {
                    return new byte[] {(byte) n};
                }
                throw new SyntaxErrorException(s);
            case HALF:
                n = RegexCompiler.parseLongInteger(s);
                if ((n ^ (n & 0xFFFF)) == 0) {
                    return new byte[] {(byte) n, (byte) (n >> 8)};
                }
                throw new SyntaxErrorException(s);
            case WORD:
                n = RegexCompiler.parseLongInteger(s);
                if ((n ^ (n & 0xFFFFFFFFL)) == 0) {
                    return new byte[] {(byte) n, (byte) (n >> 8), (byte) (n >> 16), (byte) (n >> 24)};
                }
                throw new SyntaxErrorException(s);
            case DWORD:
                n = RegexCompiler.parseLongInteger(s);
                return longToBytes(n);
            case FLOAT:
                int floatBits;
                try {
                    floatBits = Float.floatToIntBits(Float.parseFloat(s));
                } catch (NumberFormatException e) {
                    throw new SyntaxErrorException(s);
                }
                return new byte[] {
                    (byte) (floatBits & 0xFF),
                    (byte) ((floatBits >> 8) & 0xFF),
                    (byte) ((floatBits >> 16) & 0xFF),
                    (byte) ((floatBits >> 24) & 0xFF)
                };
            case DOUBLE:
                long doubleBits;
                try {
                    doubleBits = Double.doubleToLongBits(Double.parseDouble(s));
                } catch (NumberFormatException e) {
                    throw new SyntaxErrorException(s);
                }
                return longToBytes(doubleBits);
            case SPACE:
                try {
                    int spaceSize = Integer.parseInt(s);
                    return new byte[spaceSize];
                } catch (NumberFormatException e) {
                    throw new SyntaxErrorException(s);
                }
            case ASCII:
                parsedString = RegexCompiler.parseString(s);
                byte[] asciiBytes = new byte[parsedString.length()];
                for (int i = 0; i < parsedString.length(); i++) {
                    asciiBytes[i] = (byte) parsedString.charAt(i);
                }
                return asciiBytes;
            case ASCIZ:
            case STRING:
                parsedString = RegexCompiler.parseString(s);
                byte[] stringBytes = new byte[parsedString.length() + 1];
                for (int i = 0; i < parsedString.length(); i++) {
                    stringBytes[i] = (byte) parsedString.charAt(i);
                }
                stringBytes[stringBytes.length - 1] = 0;
                return stringBytes;
            default:
                throw new SyntaxErrorException(s);
        }
    }

    private static byte[] longToBytes(long n) {
        return new byte[] {
            (byte) (n & 0xFF),
            (byte) ((n >> 8) & 0xFF),
            (byte) ((n >> 16) & 0xFF),
            (byte) ((n >> 24) & 0xFF),
            (byte) ((n >> 32) & 0xFF),
            (byte) ((n >> 40) & 0xFF),
            (byte) ((n >> 48) & 0xFF),
            (byte) ((n >> 56) & 0xFF)
        };
    }
}
