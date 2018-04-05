package com.ctrlshift.commons.stream;

/**
 * A type which is both a {@link StreamMessage} and a {@link StreamWriter}. This type is mainly used by tests
 * which need to exercise both functionality.
 */
interface StreamMessageAndWriter<T> extends StreamMessage<T>, StreamWriter<T> {
}