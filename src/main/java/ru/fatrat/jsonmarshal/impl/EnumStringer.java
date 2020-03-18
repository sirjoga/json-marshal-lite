package ru.fatrat.jsonmarshal.impl;

import javax.annotation.Nonnull;

public interface EnumStringer {
    @Nonnull <T extends Enum<T>> T decode(@Nonnull String source, @Nonnull Class<T> type);
    @Nonnull String encode(@Nonnull Enum<?> source);
}
