package com.ctrlshift.server.docs;

import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

/**
 * Metadata about a named type.
 */
public interface NamedTypeInfo {

    /**
     * Returns the fully qualified type name.
     */
    @JsonProperty
    String name();

    /**
     * Returns the documentation string. If not available, an empty string is returned.
     */
    @JsonProperty
    @JsonInclude(Include.NON_NULL)
    @Nullable
    String docString();

    /**
     * Returns all enum, struct and exception types referred by this type.
     */
    default Set<TypeSignature> findNamedTypes() {
        return ImmutableSet.of();
    }
}