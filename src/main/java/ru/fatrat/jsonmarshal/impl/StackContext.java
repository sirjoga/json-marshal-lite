package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonMarshalStackContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

/**
 * Stack context implementor.
 */
public class StackContext implements JsonMarshalStackContext {

    private final Deque<Element> stack = new ArrayDeque<>();

    private static class Element {
        @Nullable
        String stringId;
        int intId;

        Element(@Nonnull String id) {
            this.stringId = id;
        }

        Element(int id) {
            this.intId = id;
        }

        @Override
        public String toString() {
            if (stringId != null) return String.format(".%s", stringId);
            return String.format("[%d]", intId);
        }
    }

    @Override
    public void pushArrayElementId(int id) {
        stack.addLast(new Element(id));
    }

    @Override
    public void pushObjectFieldElementId(@Nonnull String id) {
        stack.addLast(new Element(id));
    }

    @Override
    public void popElementId() {
        stack.removeLast();
    }

    @Override
    public String toString() {
        return "ROOT" + stack.stream()
                .map(Object::toString)
                .collect(Collectors.joining(""));
    }
}
