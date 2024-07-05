package rarsreborn.core.core.environment;

import rarsreborn.core.exceptions.execution.ExecutionException;

public interface IExecutionEnvironment {
    void call() throws ExecutionException;

    void break_() throws ExecutionException;
}
