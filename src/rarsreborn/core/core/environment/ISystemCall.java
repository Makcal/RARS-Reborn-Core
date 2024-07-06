package rarsreborn.core.core.environment;

import rarsreborn.core.exceptions.execution.ExecutionException;

public interface ISystemCall {
    void call() throws ExecutionException;
}
