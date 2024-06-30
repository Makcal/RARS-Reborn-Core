package rarsreborn.core.core.instruction;

import rarsreborn.core.core.program.LinkRequest;
import rarsreborn.core.exceptions.linking.LinkingException;

public interface ILinkableInstruction extends IInstruction {
    void link(long address) throws LinkingException;

    LinkRequest getLinkRequest();
}
