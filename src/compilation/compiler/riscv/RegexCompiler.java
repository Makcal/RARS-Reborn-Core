package compilation.compiler.riscv;

import compilation.compiler.ICompiler;
import compilation.compiler.ICompilerBuilder;
import core.instruction.IInstruction;
import core.instruction.ILinkableInstruction;
import core.program.*;
import core.register.IRegister;
import core.register.IRegisterFile;
import core.riscvprogram.DataBlock;
import exceptions.compilation.*;

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
        source = source.strip().replaceAll(" *\n+ *", "\n").replaceAll(" +", " ").replaceAll(" ?, ?", ",");

        CompilingContext context = new CompilingContext();

        for (String line : source.split("\n")) {
            if (line.isEmpty()) continue;

            if (line.charAt(0) == '.') {
                processDirective(line);
                continue;
            }

            if (parsingState == ParsingState.TEXT) {
                Matcher labelMatcher = LABEL_PATTERN.matcher(line);
                if (labelMatcher.matches()) {
                    context.symbolTable.addSymbol(labelMatcher.group(1), context.instructions.size());
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
            context.symbolTable.addSymbol(block.label(), context.data.size());
        }
        byte[] blockValue = block.getValue();
        for (byte b : blockValue) {
            context.data.add(b);
        }
    }

    protected void loadInstruction(CompilingContext context, IInstruction instruction) {
        if (instruction instanceof ILinkableInstruction) {
            context.relocationTable.addRequest(
                context.instructions.size(),
                ((ILinkableInstruction) instruction).getLinkRequest()
            );
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
        private final Map<String, IRegister> registers = new HashMap<>();
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
        public RegexCompilerBuilder registerRegister(IRegister register) {
            registers.put(register.getName(), register);
            return this;
        }

        @Override
        public RegexCompilerBuilder registerRegistersFromFile(IRegisterFile<?> registerFile) {
            for (IRegister register : registerFile.getAllRegisters()) {
                registerRegister(register);
            }
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
    }
}
