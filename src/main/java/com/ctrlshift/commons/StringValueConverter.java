package com.ctrlshift.commons;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

import io.netty.handler.codec.DateFormatter;
import io.netty.handler.codec.ValueConverter;

/**
 * Converts to/from native types, general {@link Object}, and {@link CharSequence}s.
 */
final class StringValueConverter implements ValueConverter<String> {

    // Forked from Netty at f755e584638e20a4ae62466dd4b7a14954650348 (CharSequenceConverter) and
    // 942b993f2b9781ff2126ff92a6be5b975dfc72ed (DefaultHttpHeaders.HeaderValueConverter)

    static final StringValueConverter INSTANCE = new StringValueConverter();

    private StringValueConverter() {}

    @Nullable
    @Override
    public String convertObject(@Nullable Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Date) {
            return DateFormatter.format((Date) value);
        }

        if (value instanceof Calendar) {
            return DateFormatter.format(((Calendar) value).getTime());
        }

        if (value instanceof Instant) {
            return DateFormatter.format(new Date(((Instant) value).toEpochMilli()));
        }

        return value.toString();
    }

    @Override
    public String convertInt(int value) {
        return String.valueOf(value);
    }

    @Override
    public String convertLong(long value) {
        return String.valueOf(value);
    }

    @Override
    public String convertDouble(double value) {
        return String.valueOf(value);
    }

    @Override
    public String convertChar(char value) {
        return String.valueOf(value);
    }

    @Override
    public String convertBoolean(boolean value) {
        return String.valueOf(value);
    }

    @Override
    public String convertFloat(float value) {
        return String.valueOf(value);
    }

    @Override
    public boolean convertToBoolean(String value) {
        return Boolean.parseBoolean(value);
    }

    @Override
    public String convertByte(byte value) {
        return String.valueOf(value & 0xFF);
    }

    @Override
    public byte convertToByte(String value) {
        return (byte) value.charAt(0);
    }

    @Override
    public char convertToChar(String value) {
        return value.charAt(0);
    }

    @Override
    public String convertShort(short value) {
        return String.valueOf(value);
    }

    @Override
    public short convertToShort(String value) {
        return Short.valueOf(value);
    }

    @Override
    public int convertToInt(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public long convertToLong(String value) {
        return Long.parseLong(value);
    }

    @Override
    public String convertTimeMillis(long value) {
        return DateFormatter.format(new Date(value));
    }

    @Override
    public long convertToTimeMillis(String value) {
        final Date date = DateFormatter.parseHttpDate(value);
        if (date == null) {
            throw new IllegalArgumentException("not a date: " + value);
        }
        return date.getTime();
    }

    @Override
    public float convertToFloat(String value) {
        return Float.valueOf(value);
    }

    @Override
    public double convertToDouble(String value) {
        return Double.valueOf(value);
    }
}
