package com.ctrlshift.commons;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import io.netty.util.AsciiString;

/**
 * Jackson {@link JsonSerializer} for {@link HttpHeaders}.
 */
public final class HttpHeadersJsonSerializer extends StdSerializer<HttpHeaders> {

    private static final long serialVersionUID = 4459242879396343114L;

    /**
     * Creates a new instance.
     */
    public HttpHeadersJsonSerializer() {
        super(HttpHeaders.class);
    }

    @Override
    public void serialize(HttpHeaders headers, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {

        gen.writeStartObject();

        for (AsciiString name : headers.names()) {
            gen.writeFieldName(name.toString());
            final List<String> values = headers.getAll(name);
            if (values.size() == 1) {
                gen.writeString(values.get(0));
            } else {
                gen.writeStartArray();
                for (String value : values) {
                    gen.writeString(value);
                }
                gen.writeEndArray();
            }
        }

        gen.writeEndObject();
    }
}
