package com.wanted.safewallet.domain.category.web.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wanted.safewallet.docs.common.AbstractRestDocsTest;
import com.wanted.safewallet.docs.common.DocsPopupLinkGenerator;
import com.wanted.safewallet.docs.common.DocsPopupLinkGenerator.DocsPopupInfo;
import com.wanted.safewallet.domain.category.business.service.CategoryService;
import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.domain.category.web.dto.response.CategoryListResponseDto;
import com.wanted.safewallet.domain.category.web.dto.response.CategoryListResponseDto.CategoryResponseDto;
import com.wanted.safewallet.utils.auth.WithMockCustomUser;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

@WithMockCustomUser
@WebMvcTest(CategoryController.class)
class CategoryControllerTest extends AbstractRestDocsTest {

    @MockBean
    CategoryService categoryService;

    @DisplayName("카테고리 목록 조회 테스트 : 성공")
    @Test
    void getCategoryList() throws Exception {
        //given
        CategoryListResponseDto dto = new CategoryListResponseDto(
            List.of(new CategoryResponseDto(1L, CategoryType.FOOD),
                new CategoryResponseDto(2L, CategoryType.TRAFFIC)));
        given(categoryService.getCategoryList()).willReturn(dto);

        //when, then
        restDocsMockMvc.perform(get("/api/categories")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.categoryList", hasSize(2)))
            .andDo(restDocs.document(
                responseFields(
                    beneathPath("data").withSubsectionId("data"),
                    fieldWithPath("categoryList").description("카테고리 리스트"),
                    fieldWithPath("categoryList[].categoryId").description("카테고리 id"),
                    fieldWithPath("categoryList[].type").description(DocsPopupLinkGenerator
                        .generatePopupLink(DocsPopupInfo.CATEGORY_TYPE)))
            ));
        then(categoryService).should(times(1)).getCategoryList();
    }
}
