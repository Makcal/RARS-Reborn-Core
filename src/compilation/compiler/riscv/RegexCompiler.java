package compilation.compiler.riscv;

import compilation.compiler.ICompiler;
import compilation.compiler.ICompilerBuilder;
import core.instruction.IInstruction;
import core.register.IRegister;
import core.register.IRegisterFile;
import core.riscvprogram.DataBlock;
import core.program.IDataBlock;
import core.program.IObjectFile;
import core.program.IProgramBuilder;
import exceptions.compilation.CompilationException;
import exceptions.compilation.SyntaxErrorException;
import exceptions.compilation.UnknownInstructionException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexCompiler implements ICompiler {
    public static final Pattern DATA_STATEMENT_PATTERN =
        Pattern.compile("(?:([a-zA-Z_]\\w*) *: *)?(\\.\\w+) +(.*)");

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

        List<IInstruction> instructions = new LinkedList<>();
        List<IDataBlock> dataBlocks = new LinkedList<>();

        for (String line : source.split("\n")) {
            if (line.isEmpty()) continue;

            switch (line) {
                case ".data":
                    parsingState = ParsingState.DATA;
                    continue;
                case ".text":
                    parsingState = ParsingState.TEXT;
                    continue;
            }

            switch (parsingState) {
                case TEXT -> instructions.add(parseInstruction(line));
                case DATA -> dataBlocks.add(parseDataBlock(line));
            }
        }

        return programBuilder
            .addInstructions(instructions)
            .addData(dataBlocks)
            .build();
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

    protected IDataBlock parseDataBlock(String line) throws CompilationException {
        Matcher matcher = DATA_STATEMENT_PATTERN.matcher(line);
        if (!matcher.matches())
            throw new SyntaxErrorException(line);

        RiscVDataDirective directive = RiscVDataDirective.parseName(matcher.group(2));
        byte[] value = directive.parseValue(matcher.group(3));
        return new DataBlock(value.length, directive.alignment, value);
    }

    protected enum ParsingState {
        DATA,
        TEXT,
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
