package com.wanted.safewallet.domain.category.web.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryListResponseDto {

    private List<String> categoryList;
}
