package compilation.decoder;

import core.instruction.IInstruction;

public record DecodingResult(IInstruction instruction, int bytesConsumed) {}
