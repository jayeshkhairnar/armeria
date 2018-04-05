package com.ctrlshift.server.docs;

import static java.util.Objects.requireNonNull;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * Metadata about an enum type.
 */
public final class EnumInfo implements NamedTypeInfo {

    private final String name;
    private final List<EnumValueInfo> values;
    @Nullable
    private final String docString;

    /**
     * Creates a new instance.
     */
    public EnumInfo(String name, Class<? extends Enum<?>> enumType) {
        this(name, enumType, null);
    }

    /**
     * Creates a new instance.
     */
    public EnumInfo(String name, Class<? extends Enum<?>> enumType, @Nullable String docString) {
        this(name, toEnumValues(enumType), docString);
    }

    /**
     * Creates a new instance.
     */
    public EnumInfo(String name, Iterable<EnumValueInfo> values) {
        this(name, values, null);
    }

    /**
     * Creates a new instance.
     */
    public EnumInfo(String name, Iterable<EnumValueInfo> values, @Nullable String docString) {
        this.name = requireNonNull(name, "name");
        this.values = ImmutableList.copyOf(requireNonNull(values, "values"));
        this.docString = Strings.emptyToNull(docString);
    }

    @Override
    public String name() {
        return name;
    }

    /**
     * Returns the constant values defined by the type.
     */
    @JsonProperty
    public List<EnumValueInfo> values() {
        return values;
    }

    @Override
    public String docString() {
        return docString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final EnumInfo that = (EnumInfo) o;
        return name.equals(that.name) && values.equals(that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, values);
    }

    @Override
    public String toString() {
        return name;
    }

    private static Iterable<EnumValueInfo> toEnumValues(Class<? extends Enum<?>> enumType) {
        final Class<?> rawEnumType = requireNonNull(enumType, "enumType");
        @SuppressWarnings({ "unchecked", "rawtypes" })
        final Set<Enum> values = EnumSet.allOf((Class<Enum>) rawEnumType);
        return values.stream().map(e -> new EnumValueInfo(e.name()))::iterator;
    }
}
