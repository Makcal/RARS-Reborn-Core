package core.program;

import core.instruction.ILinkableInstruction;

public record LinkRequest(ILinkableInstruction instruction, String label) {}
