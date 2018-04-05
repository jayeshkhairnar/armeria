package com.ctrlshift.commons;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import com.google.common.base.Ascii;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

/**
 * Registers the {@link SerializationFormat}s dynamically via Java SPI (Service Provider Interface).
 */
public abstract class SerializationFormatProvider {

    /**
     * Returns the {@link Entry}s to register as {@link SerializationFormat}s.
     */
    protected abstract Set<Entry> entries();

    /**
     * A registration entry of a {@link SerializationFormat}.
     */
    protected static final class Entry implements Comparable<Entry> {
        final String uriText;
        final MediaType primaryMediaType;
        final MediaTypeSet mediaTypes;

        /**
         * Creates a new instance.
         */
        public Entry(String uriText, MediaType primaryMediaType, MediaType... alternativeMediaTypes) {
            this.uriText = Ascii.toLowerCase(requireNonNull(uriText, "uriText"));
            this.primaryMediaType = requireNonNull(primaryMediaType, "primaryMediaType");
            mediaTypes = new MediaTypeSet(ImmutableList.<MediaType>builder()
                    .add(primaryMediaType)
                    .add(requireNonNull(alternativeMediaTypes, "alternativeMediaTypes"))
                    .build());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || obj.getClass() != Entry.class) {
                return false;
            }

            return uriText.equals(((Entry) obj).uriText);
        }

        @Override
        public int compareTo(Entry o) {
            return uriText.compareTo(o.uriText);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                              .add("uriText", uriText)
                              .add("mediaTypes", mediaTypes).toString();
        }

        @Override
        public int hashCode() {
            return uriText.hashCode();
        }
    }
}