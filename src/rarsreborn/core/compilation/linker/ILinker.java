package rarsreborn.core.compilation.linker;

import rarsreborn.core.core.program.IExecutable;
import rarsreborn.core.core.program.IObjectFile;
import rarsreborn.core.exceptions.linking.LinkingException;

public interface ILinker {
    IExecutable link(IObjectFile... objectFiles) throws LinkingException;
}
