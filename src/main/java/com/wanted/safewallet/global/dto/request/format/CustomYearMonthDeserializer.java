package com.wanted.safewallet.global.dto.request.format;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomYearMonthDeserializer extends JsonDeserializer<YearMonth> {

    private static final String HYPHEN_PATTERN = "yyyy-M";
    private static final String SLASH_PATTERN = "yyyy/M";
    private static final String DOT_PATTERN = "yyyy.M";
    private static final List<DateTimeFormatter> formatters = List.of(
        DateTimeFormatter.ofPattern(HYPHEN_PATTERN),
        DateTimeFormatter.ofPattern(SLASH_PATTERN),
        DateTimeFormatter.ofPattern(DOT_PATTERN));

    @Override
    public YearMonth deserialize(JsonParser parser, DeserializationContext context)
        throws IOException {
        for (DateTimeFormatter formatter : formatters) {
            try {
                YearMonthDeserializer deserializer = new YearMonthDeserializer(formatter);
                return deserializer.deserialize(parser, context);
            } catch (Exception e) {
            }
        }
        throw new JsonParseException(parser, "Unable to parse date: [" + parser.getValueAsString()
            + "]. Supported formats: " + List.of(HYPHEN_PATTERN, SLASH_PATTERN, DOT_PATTERN));
    }
}
