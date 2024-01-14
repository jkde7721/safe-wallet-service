package com.wanted.safewallet.global.dto.response.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Aspect
@Component
public class PaginationAdvice {

    private final PageStore pageStore;

    @AfterReturning(value = "execution(org.springframework.data.domain.Page com.wanted.safewallet.domain..repository..*.*(..))", returning = "page")
    public void processPagination(Page<?> page) {
        pageStore.setPage(page); //ThreadLocal에 저장
    }
}
