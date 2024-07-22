package rarsreborn.core.compilation.compiler.riscv;

import rarsreborn.core.compilation.compiler.ICompiler;
import rarsreborn.core.compilation.compiler.ICompilerBuilder;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.ILinkableInstruction;
import rarsreborn.core.core.program.*;
import rarsreborn.core.core.register.IRegister;
import rarsreborn.core.core.register.IRegisterCollection;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.program.riscvprogram.DataBlock;
import rarsreborn.core.exceptions.compilation.*;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexCompiler implements ICompiler {
    public static final Pattern DATA_STATEMENT_PATTERN =
        Pattern.compile("(?:([a-zA-Z_]\\w*) *: *)?(\\.\\w+) +(.*)");
    public static final Pattern LABEL_PATTERN = Pattern.compile("([a-zA-Z_]\\w*) *:");

    protected final IProgramBuilder programBuilder;
    protected final Map<String, IInstructionRegexParser<?>> parsers;

    protected ParsingState parsingState = ParsingState.TEXT;

    protected RegexCompiler(IProgramBuilder programBuilder, Map<String, IInstructionRegexParser<?>> parsers) {
        this.programBuilder = programBuilder;
        this.parsers = parsers;
    }

    @Override
    public IObjectFile compile(String source) throws CompilationException {
        source = source
            .strip()
            .replaceAll("\t", " ")
            .replaceAll(" *\n+ *", "\n")
            .replaceAll(" +", " ")
            .replaceAll(" ?, ?", ",");

        CompilingContext context = new CompilingContext();

        for (String line : source.split("\n")) {
            if (line.isEmpty() || line.charAt(0) == '#') continue;

            if (line.charAt(0) == '.') {
                processDirective(line);
                continue;
            }

            if (parsingState == ParsingState.TEXT) {
                Matcher labelMatcher = LABEL_PATTERN.matcher(line);
                if (labelMatcher.matches()) {
                    context.symbolTable.addSymbol(
                        new Symbol(
                            SymbolType.INSTRUCTION_LABEL,
                            labelMatcher.group(1),
                            context.instructions.size()
                        )
                    );
                    continue;
                }
            }

            switch (parsingState) {
                case TEXT -> {
                    IInstruction instruction = parseInstruction(line);
                    loadInstruction(context, instruction);
                }
                case DATA -> {
                    DataBlock dataBlock = parseDataBlock(line);
                    loadDataBlock(context, dataBlock);
                }
            }
        }

        return assemble(context);
    }

    protected void processDirective(String directive) throws UnknownDirectiveException {
        switch (directive) {
            case ".data":
                parsingState = ParsingState.DATA;
                break;
            case ".text":
                parsingState = ParsingState.TEXT;
                break;
            default:
                throw new UnknownDirectiveException(directive);
        }
    }

    protected void loadDataBlock(CompilingContext context, DataBlock block) throws LabelDuplicateException {
        long pointer = context.data.size();
        if (pointer % block.getAlignment() != 0) {
            long offset = block.getAlignment() - (pointer % block.getAlignment());
            for (int i = 0; i < offset; i++) {
                context.data.add((byte) 0);
            }
        }
        if (block.label() != null) {
            context.symbolTable.addSymbol(new Symbol(SymbolType.DATA, block.label(), context.data.size()));
        }
        byte[] blockValue = block.getValue();
        for (byte b : blockValue) {
            context.data.add(b);
        }
    }

    protected void loadInstruction(CompilingContext context, IInstruction instruction) {
        if (instruction instanceof ILinkableInstruction) {
            LinkRequest request = ((ILinkableInstruction) instruction).getLinkRequest();
            if (request != null)
                context.relocationTable.addRequest(context.instructions.size(), request);
        }
        for (byte b : instruction.serialize()) {
            context.instructions.add(b);
        }
    }

    protected IInstruction parseInstruction(String line) throws CompilationException {
        String[] lineSplit = line.split(" ", 2);
        String instructionName = lineSplit[0];
        IInstructionRegexParser<?> parser = parsers.get(instructionName);
        if (parser == null) {
            throw new UnknownInstructionException(instructionName);
        }
        return parser.parse(lineSplit.length == 2 ? lineSplit[1] : "");
    }

    protected DataBlock parseDataBlock(String line) throws CompilationException {
        Matcher matcher = DATA_STATEMENT_PATTERN.matcher(line);
        if (!matcher.matches())
            throw new SyntaxErrorException(line);

        RiscVDataDirective directive = RiscVDataDirective.parseName(matcher.group(2));
        byte[] value = directive.parseValue(matcher.group(3));
        return new DataBlock(value.length, directive.alignment, value, matcher.group(1));
    }

    protected IObjectFile assemble(CompilingContext context) {
        return programBuilder
            .addData(listToArray(context.data))
            .addInstructions(listToArray(context.instructions))
            .addSymbolTable(context.symbolTable)
            .addRelocationTable(context.relocationTable)
            .build();
    }

    protected byte[] listToArray(ArrayList<Byte> list) {
        byte[] array = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    protected enum ParsingState {
        DATA,
        TEXT,
    }

    protected record CompilingContext(
        ArrayList<Byte> data,
        ArrayList<Byte> instructions,
        SymbolTable symbolTable,
        RelocationTable relocationTable
    ) {
        public CompilingContext() {
            this(new ArrayList<>(), new ArrayList<>(), new SymbolTable(), new RelocationTable());
        }
    }

    public static class RegexCompilerBuilder implements ICompilerBuilder {
        private final RegisterCollection registers = new RegisterCollection();
        private final Map<String, IInstructionRegexParser<?>> parsers = new HashMap<>();
        private IProgramBuilder programBuilder;

        @Override
        public ICompiler build() {
            return new RegexCompiler(programBuilder, new HashMap<>(parsers));
        }

        @Override
        public ICompilerBuilder setProgramBuilder(IProgramBuilder programBuilder) {
            this.programBuilder = programBuilder;
            return this;
        }

        @Override
        public RegexCompilerBuilder registerRegistersFromFile(IRegisterFile<?> registerFile) {
            registerFile.getAllRegisters().forEach(registers::addRegister);
            return this;
        }

        @Override
        public <TInstruction extends IInstruction>
        RegexCompilerBuilder registerInstruction(
                String instructionName,
                IInstructionRegexParser<TInstruction> parser
        ) {
            parser.attachRegisters(registers);
            parsers.put(instructionName, parser);
            return this;
        }

        public static class RegisterCollection implements IRegisterCollection {
            protected final HashMap<String, IRegister> registerMap = new HashMap<>();

            @Override
            public IRegister findRegister(String name) throws IllegalRegisterException {
                IRegister register = registerMap.get(name);
                if (register != null)
                    return register;
                throw new IllegalRegisterException(name);
            }

            public void addRegister(IRegister register) {
                registerMap.put(register.getName(), register);
                registerMap.put(register.getNumericName(), register);
            }
        }
    }

    public static long parseLongInteger(String s) throws SyntaxErrorException {
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
    
    public static float parseFloat(String s) throws SyntaxErrorException {
        s = s.toLowerCase();
        if (s.equals("nan")) {
            return Float.NaN;
        } else if (s.matches("\\+?\\s*inf(inity)?")) {
            return Float.POSITIVE_INFINITY;
        } else if (s.matches("-\\s*inf(inity)?")) {
            return Float.NEGATIVE_INFINITY;
        } else {
            try {
                return Float.parseFloat(s);
            } catch (NumberFormatException e) {
                throw new SyntaxErrorException(s);
            }
        }
    }
    
    public static double parseDouble(String s) throws SyntaxErrorException {
        s = s.toLowerCase();
        if (s.equals("nan")) {
            return Double.NaN;
        } else if (s.matches("\\+?\\s*inf(inity)?")) {
            return Double.POSITIVE_INFINITY;
        } else if (s.matches("-\\s*inf(inity)?")) {
            return Double.NEGATIVE_INFINITY;
        } else {
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                throw new SyntaxErrorException(s);
            }
        }
    }

    public static String parseString(String s) throws SyntaxErrorException {
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
