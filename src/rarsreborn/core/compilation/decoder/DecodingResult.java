package rarsreborn.core.compilation.decoder;

import rarsreborn.core.core.instruction.IInstruction;

public record DecodingResult(IInstruction instruction, int bytesConsumed) {}
