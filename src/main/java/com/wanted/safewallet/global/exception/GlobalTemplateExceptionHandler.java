package com.wanted.safewallet.global.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalTemplateExceptionHandler {

    @ExceptionHandler(BusinessTemplateException.class)
    public String handleBusinessTemplateException(BusinessTemplateException e, Model model) {
        model.addAllAttributes(e.getAttributes());
        model.addAttribute("message", e.getMessage());
        return e.getTemplate();
    }
}
