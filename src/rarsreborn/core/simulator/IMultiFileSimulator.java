package rarsreborn.core.simulator;

import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.linking.LinkingException;

public interface IMultiFileSimulator extends ISimulator {
    void compile(String ...files) throws CompilationException, LinkingException;
}
