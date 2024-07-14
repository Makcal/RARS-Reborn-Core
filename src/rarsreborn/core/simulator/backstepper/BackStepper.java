package rarsreborn.core.simulator.backstepper;

import rarsreborn.core.exceptions.NoBackStepsLeftException;
import rarsreborn.core.exceptions.execution.ExecutionException;

import java.util.ArrayDeque;
import java.util.Deque;

public class BackStepper implements IBackStepper {
    private final int size;
    private final ArrayDeque<Change> stack;

    public BackStepper(int size) {
        this.size = size;
        stack = new ArrayDeque<>(size);
    }

    @Override
    public void addStep() {
        if (stack.size() == size) {
            stack.poll();
        }
        stack.push(new Change());
    }

    @Override
    public void addChange(IRevertible revertible) {
        Change change = stack.peek();
        if (change == null) {
            throw new RuntimeException("Nowhere to push a change");
        }
        change.addChange(revertible);
    }

    @Override
    public void revert() throws NoBackStepsLeftException, ExecutionException {
        Change change = stack.pollFirst();
        if (change == null) {
            throw new NoBackStepsLeftException();
        }
        change.revert();
    }

    @Override
    public void reset() {
        stack.clear();
    }

    public static class Change implements IRevertible {
        private final Deque<IRevertible> changes = new ArrayDeque<>();

        @Override
        public void revert() throws ExecutionException {
            while (!changes.isEmpty()) {
                changes.pop().revert();
            }
        }

        public void addChange(IRevertible revertible) {
            changes.push(revertible);
        }
    }
}
