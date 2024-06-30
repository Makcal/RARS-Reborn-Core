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
                n = parseInteger(s);
                if ((n & 0xFF) == 0) {
                    return new byte[] {(byte) n};
                }
                throw new SyntaxErrorException(s);
            case HALF:
                n = parseInteger(s);
                if ((n & 0xFFFF) == 0) {
                    return new byte[] {(byte) n, (byte) (n >> 8)};
                }
                throw new SyntaxErrorException(s);
            case WORD:
                n = parseInteger(s);
                if ((n & 0xFFFFFFFFL) == 0) {
                    return new byte[] {(byte) n, (byte) (n >> 8), (byte) (n >> 16), (byte) (n >> 24)};
                }
                throw new SyntaxErrorException(s);
            case DWORD:
                n = parseInteger(s);
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
                parsedString = parseString(s);
                byte[] asciiBytes = new byte[parsedString.length()];
                for (int i = 0; i < parsedString.length(); i++) {
                    asciiBytes[i] = (byte) parsedString.charAt(i);
                }
                return asciiBytes;
            case ASCIZ:
            case STRING:
                parsedString = parseString(s);
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

    private static long parseInteger(String s) throws SyntaxErrorException {
        if (s.charAt(0) == '\'' && s.charAt(s.length() - 1) == '\'') {
            String middle = s.substring(1, s.length() - 1);
            if (middle.charAt(0) == '\\') {
                if (middle.length() != 2)
                    throw new SyntaxErrorException(s);

                return switch (middle.charAt(1)) {
                    case 'n' -> '\n';
                    case 't' -> '\t';
                    case 'r' -> '\r';
                    case '\\' -> '\\';
                    case '\'' -> '\'';
                    case '"' -> '"';
                    case '0' -> '\0';
                    default -> throw new SyntaxErrorException(s);
                };
            }
            else {
                if (middle.length() != 1)
                    throw new SyntaxErrorException(s);
                return middle.charAt(0);
            }
        }

        try {
            boolean negate = false;
            if (s.charAt(0) == '-') {
                negate = true;
                s = s.substring(1);
            }
            else if (s.charAt(0) == '+')
                s = s.substring(1);

            long n;
            if (s.startsWith("0x")) n = Long.parseLong(s.substring(2), 16);
            else if (s.startsWith("0b")) n = Long.parseLong(s.substring(2), 2);
            else if (s.startsWith("0")) n = Long.parseLong(s, 8);
            else n = Long.parseLong(s);

            return negate ? -n : n;
        } catch (NumberFormatException e) {
            throw new SyntaxErrorException(s);
        }
    }

    private static String parseString(String s) throws SyntaxErrorException {
        if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
            StringBuilder builder = new StringBuilder(s.length() - 2);
            char[] chars = s.substring(1, s.length() - 1).toCharArray();
            for (int i = 0; i < chars.length; i++) {
                char ch = chars[i];
                if (ch == '\\') {
                    if (i + 1 == chars.length)
                        throw new SyntaxErrorException(s);
                    char next = chars[++i];
                    switch (next) {
                        case 'n' -> builder.append('\n');
                        case 't' -> builder.append('\t');
                        case 'r' -> builder.append('\r');
                        case '\\' -> builder.append('\\');
                        case '\'' -> builder.append('\'');
                        case '"' -> builder.append('"');
                        case '0' -> builder.append('\0');
                        default -> throw new SyntaxErrorException(s);
                    }
                }
                else if (ch <= 127)
                    builder.append(ch);
                else
                    throw new SyntaxErrorException(s);
            }
            return builder.toString();
        }
        throw new SyntaxErrorException(s);
    }
}
