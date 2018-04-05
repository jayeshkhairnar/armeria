package com.ctrlshift.server.docs;

public enum FieldRequirement {
    /**
     * The field is required. The invocation will fail if the field is not specified.
     */
    REQUIRED,

    /**
     * The field is optional. The invocation will work even if the field is not specified.
     */
    OPTIONAL,

    /**
     * The requirement level is unspecified and will be handled implicitly by the serialization layer.
     */
    DEFAULT
}