package com.wanted.safewallet.global.exception;

import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BusinessTemplateException extends RuntimeException {

    private final String message;
    private final Map<String, ?> attributes;
    private final String template;

    public BusinessTemplateException(BusinessException e, String template) {
        this.message = e.getErrorCode().getMessage();
        this.attributes = Map.of();
        this.template = template;
    }

    public BusinessTemplateException(BusinessException e, Map<String, ?> attributes, String template) {
        this.message = e.getErrorCode().getMessage();
        this.attributes = attributes;
        this.template = template;
    }
}
