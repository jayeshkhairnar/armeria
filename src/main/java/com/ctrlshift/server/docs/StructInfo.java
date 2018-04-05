package com.ctrlshift.server.docs;

import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

/**
 * Metadata about a struct type.
 */
public final class StructInfo implements NamedTypeInfo {

    private final String name;
    private final List<FieldInfo> fields;
    @Nullable
    private final String docString;

    /**
     * Creates a new instance.
     */
    public StructInfo(String name, Iterable<FieldInfo> fields) {
        this(name, fields, null);
    }

    /**
     * Creates a new instance.
     */
    public StructInfo(String name, Iterable<FieldInfo> fields, @Nullable String docString) {
        this.name = requireNonNull(name, "name");
        this.fields = ImmutableList.copyOf(requireNonNull(fields, "fields"));
        this.docString = Strings.emptyToNull(docString);
    }

    @Override
    public String name() {
        return name;
    }

    /**
     * Returns the metadata about the fields of the type.
     */
    @JsonProperty
    public List<FieldInfo> fields() {
        return fields;
    }

    @Override
    public String docString() {
        return docString;
    }

    @Override
    public Set<TypeSignature> findNamedTypes() {
        final Set<TypeSignature> collectedNamedTypes = new HashSet<>();
        fields().forEach(f -> ServiceInfo.findNamedTypes(collectedNamedTypes, f.typeSignature()));
        return ImmutableSortedSet.copyOf(comparing(TypeSignature::name), collectedNamedTypes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final StructInfo that = (StructInfo) o;
        return name.equals(that.name) && fields.equals(that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fields);
    }

    @Override
    public String toString() {
        return name;
    }
}