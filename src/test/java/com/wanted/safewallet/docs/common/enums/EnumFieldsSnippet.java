package com.wanted.safewallet.docs.common.enums;

import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.AbstractFieldsSnippet;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadSubsectionExtractor;

public class EnumFieldsSnippet extends AbstractFieldsSnippet {

    private static final String TYPE = "enum"; //사용할 snippet 파일 이름 지정

    protected EnumFieldsSnippet(List<FieldDescriptor> descriptors,
        Map<String, Object> attributes, boolean ignoreUndocumentedFields,
        PayloadSubsectionExtractor<?> subsectionExtractor) {
        super(TYPE, descriptors, attributes, ignoreUndocumentedFields, subsectionExtractor);
    }

    @Override
    protected MediaType getContentType(Operation operation) {
        return operation.getResponse().getHeaders().getContentType();
    }

    @Override
    protected byte[] getContent(Operation operation) {
        return operation.getResponse().getContent();
    }
}
