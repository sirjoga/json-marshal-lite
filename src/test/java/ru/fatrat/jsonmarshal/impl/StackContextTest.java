package ru.fatrat.jsonmarshal.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StackContextTest {

    @Test
    void testToString() {
        StackContext context = new StackContext();
        context.pushArrayElementId(1);
        context.pushObjectFieldElementId("b");
        Assertions.assertEquals("ROOT[1].b", context.toString());
    }
}