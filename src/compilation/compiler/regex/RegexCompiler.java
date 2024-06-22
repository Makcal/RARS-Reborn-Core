package compilation.compiler.regex;

import compilation.compiler.ICompiler;
import compilation.compiler.ICompilerBuilder;
import exceptions.compilation.CompilationException;
import exceptions.compilation.UnknownInstructionException;
import assembler.instruction.IInstruction;
import assembler.register.IRegister;
import assembler.register.IRegisterFile;

import java.util.*;

public class RegexCompiler implements ICompiler {
    private final Map<String, IInstructionRegexParser<?>> parsers;

    private RegexCompiler(Map<String, IInstructionRegexParser<?>> parsers) {
        this.parsers = parsers;
    }

    @Override
    public List<IInstruction> compile(String source) throws CompilationException {
        source = source.strip().replaceAll(" *\n+ *", "\n").replaceAll(" +", " ").replaceAll(" ?, ?", ",");
        List<IInstruction> instructions = new LinkedList<>();
        for (String line : source.split("\n")) {
            if (line.isEmpty()) continue;

            String[] lineSplit = line.split(" ", 2);
            String instructionName = lineSplit[0];
            IInstructionRegexParser<?> parser = parsers.get(instructionName);
            if (parser == null) {
                throw new UnknownInstructionException(instructionName);
            }

            instructions.add(parser.parse(lineSplit.length == 2 ? lineSplit[1] : ""));
        }
        return instructions;
    }

    public static class RegexCompilerBuilder implements ICompilerBuilder {
        private final Map<String, IRegister> registers = new HashMap<>();
        private final Map<String, IInstructionRegexParser<?>> parsers = new HashMap<>();

        @Override
        public ICompiler build() {
            return new RegexCompiler(new HashMap<>(parsers));
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
