package com.wanted.safewallet.global.dto.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PageResponse {

    private final Integer pageNumber;
    private final Integer pageSize;
    private final Integer numberOfElements;
    private final Integer totalPages;
    private final Long totalElements;
    private final Boolean first;
    private final Boolean last;
    private final Boolean empty;

    public PageResponse(Page<?> page) {
        this.pageNumber = page.getPageable().getPageNumber() + 1; //페이지 번호는 1부터 시작
        this.pageSize = page.getPageable().getPageSize();
        this.numberOfElements = page.getNumberOfElements();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.empty = page.isEmpty();
    }
}
