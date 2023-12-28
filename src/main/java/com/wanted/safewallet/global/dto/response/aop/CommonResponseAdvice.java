package com.wanted.safewallet.global.dto.response.aop;

import com.wanted.safewallet.global.dto.response.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class CommonResponseAdvice implements ResponseBodyAdvice<Object> {

    //HttpMessageConverter가 응답값을 쓰기 직전에 호출
    @Override
    public boolean supports(MethodParameter returnType,
        Class<? extends HttpMessageConverter<?>> converterType) {
        CommonResponseContent methodContent = returnType.getExecutable()
            .getDeclaredAnnotation(CommonResponseContent.class);
        CommonResponseContent classContent = returnType.getDeclaringClass()
            .getDeclaredAnnotation(CommonResponseContent.class);
        return methodContent != null || classContent != null;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
        Class<? extends HttpMessageConverter<?>> selectedConverterType,
        ServerHttpRequest request, ServerHttpResponse response) {
        CommonResponseContent methodContent = returnType.getExecutable()
            .getDeclaredAnnotation(CommonResponseContent.class);
        CommonResponseContent classContent = returnType.getDeclaringClass()
            .getDeclaredAnnotation(CommonResponseContent.class);
        CommonResponseContent content = methodContent == null ? classContent : methodContent;

        response.setStatusCode(content.status());
        return new CommonResponse<>(content.status().value(), content.status().name(),
            content.message(), body);
    }
}
