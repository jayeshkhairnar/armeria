package com.ctrlshift.server.docs;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Jackson {@link JsonSerializer} for {@link TypeSignature}.
 */
public class TypeSignatureJsonSerializer extends StdSerializer<TypeSignature> {

    private static final long serialVersionUID = 5186823627317402798L;

    /**
     * Creates a new instance.
     */
    public TypeSignatureJsonSerializer() {
        super(TypeSignature.class);
    }

    @Override
    public void serialize(TypeSignature typeSignature, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {

        gen.writeString(typeSignature.signature());
    }
}
