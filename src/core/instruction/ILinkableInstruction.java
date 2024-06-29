package core.instruction;

import core.program.LinkRequest;
import exceptions.linking.LinkingException;

public interface ILinkableInstruction extends IInstruction {
    void link(long address) throws LinkingException;

    LinkRequest getLinkRequest();
}
