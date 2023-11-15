package com.wanted.safewallet.domain.category.web.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wanted.safewallet.domain.category.business.service.CategoryService;
import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.domain.category.web.dto.response.CategoryListResponseDto;
import com.wanted.safewallet.domain.category.web.dto.response.CategoryListResponseDto.CategoryResponseDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @MockBean
    CategoryService categoryService;

    @Autowired
    MockMvc mockMvc;

    @DisplayName("카테고리 목록 조회 테스트 : 성공")
    @Test
    void getCategoryList() throws Exception {
        //given
        CategoryListResponseDto dto = new CategoryListResponseDto(
            List.of(new CategoryResponseDto(1L, CategoryType.FOOD),
                new CategoryResponseDto(2L, CategoryType.TRAFFIC)));
        given(categoryService.getCategoryList()).willReturn(dto);

        //when, then
        mockMvc.perform(get("/api/categories")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.categoryList", hasSize(2)))
            .andDo(print());
        then(categoryService).should(times(1)).getCategoryList();
    }
}
