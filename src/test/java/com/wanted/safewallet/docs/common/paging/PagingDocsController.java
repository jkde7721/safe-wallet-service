package com.wanted.safewallet.docs.common.paging;

import com.wanted.safewallet.global.dto.response.PageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paging")
public class PagingDocsController {

    @GetMapping
    public PageResponse getPaging() {
        return PageResponse.builder()
            .pageNumber(1).pageSize(3).numberOfElements(3).totalPages(4).totalElements(12L)
            .first(true).last(false).empty(false).build();
    }
}
