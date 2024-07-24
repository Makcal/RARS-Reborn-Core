package rarsreborn.core.core.environment;

import rarsreborn.core.event.IObservable;
import rarsreborn.core.exceptions.execution.ExecutionException;

public interface IExecutionEnvironment extends IObservable {
    void call() throws ExecutionException;

    void break_() throws ExecutionException;

    void reset();
}
