package compilation.linker;

import core.program.IExecutable;
import core.program.IObjectFile;
import exceptions.linking.LinkingException;

public interface ILinker {
    IExecutable link(IObjectFile... objectFiles) throws LinkingException;
}
