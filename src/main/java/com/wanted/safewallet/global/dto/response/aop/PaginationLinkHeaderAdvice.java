package com.wanted.safewallet.global.dto.response.aop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
@RestControllerAdvice
public class PaginationLinkHeaderAdvice implements ResponseBodyAdvice<Object> {

    private final PageStore pageStore;

    @Value("${spring.data.web.pageable.one-indexed-parameters}")
    private boolean oneIndexedPage;

    private static final String PAGE_PARAM_NAME = "page";
    private static final String SIZE_PARAM_NAME = "size";

    @Override
    public boolean supports(MethodParameter returnType,
        Class<? extends HttpMessageConverter<?>> converterType) {
        return Arrays.stream(returnType.getExecutable().getParameterTypes())
            .anyMatch(parameterType -> parameterType.isAssignableFrom(Pageable.class));
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
        Class<? extends HttpMessageConverter<?>> selectedConverterType,
        ServerHttpRequest request, ServerHttpResponse response) {
        Page<?> page = pageStore.getPage();
        response.getHeaders().add(HttpHeaders.LINK, getPageLinks(page));
        return body;
    }

    private String getPageLinks(Page<?> page) {
        if (page == null) return "";

        int pageNumber = page.getNumber() + (oneIndexedPage ? 1 : 0);
        List<String> links = new ArrayList<>();

        if (!page.isFirst()) {
            final String firstPageLink = getPageLink(oneIndexedPage ? 1 : 0, page.getSize());
            links.add("<" + firstPageLink  + ">; rel=\"first\"");
        }
        if (page.hasPrevious()) {
            final String prevPageLink = getPageLink(pageNumber - 1, page.getSize());
            links.add("<" + prevPageLink + ">; rel=\"prev\"");
        }
        if (page.hasNext()) {
            final String nextPageLink = getPageLink(pageNumber + 1, page.getSize());
            links.add("<" + nextPageLink + ">; rel=\"next\"");
        }
        if (!page.isLast()) {
            final String lastPageLink = getPageLink(page.getTotalPages() - (oneIndexedPage ? 0 : 1), page.getSize());
            links.add("<" + lastPageLink + ">; rel=\"last\"");
        }
        return String.join(",", links);
    }

    private String getPageLink(int page, int size) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
            .replaceQueryParam(PAGE_PARAM_NAME, page)
            .replaceQueryParam(SIZE_PARAM_NAME, size)
            .build().encode().toUriString();
    }
}
