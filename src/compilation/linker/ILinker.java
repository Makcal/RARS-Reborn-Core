package compilation.linker;

import core.program.IExecutable;
import core.program.IObjectFile;

public interface ILinker {
    IExecutable link(IObjectFile... objectFiles);
}
